package com.brody.arxiv.work.summary

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.brody.arxiv.core.common.utils.sizeInBytes
import com.brody.arxiv.core.notifications.ArxivNotificationManager
import com.brody.arxiv.core.pdf.download.PdfDownloadManager
import com.brody.arxiv.core.pdf.extract.extractors.SimplePdfTextExtractor
import com.brody.arxiv.core.threading.ApplicationScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.shared.settings.ai.domain.usecases.GetKeyUseCase
import com.brody.arxiv.shared.settings.ai.domain.usecases.SanityCheckUseCase
import com.brody.arxiv.shared.settings.ai.domain.usecases.SettingsAiUseCase
import com.brody.arxiv.shared.settings.ai.models.data.toLangdroidModel
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import com.brody.arxiv.shared.summary.domain.usecases.GenerateSummaryUseCase
import com.brody.arxiv.shared.summary.domain.usecases.GetSummarySettingsUseCase
import com.brody.arxiv.shared.summary.domain.usecases.SaveSummaryUseCase
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import com.brody.arxiv.work.summary.converters.WorkerSerializableConverter
import com.brody.arxiv.work.summary.converters.WorkerStateConverter
import com.brody.arxiv.work.summary.converters.createLanguageText
import com.brody.arxiv.work.summary.exceptions.ExpiredKeyException
import com.brody.arxiv.work.summary.exceptions.NullApiKeyException
import com.brody.arxiv.work.summary.exceptions.SummaryException
import com.brody.arxiv.work.summary.models.SummaryWorkerPromptsInfo
import com.brody.arxiv.work.summary.models.SummaryWorkerState
import com.brody.arxiv.work.summary.converters.toLangdroidConfig
import com.brody.arxiv.work.summary.models.toProcessingText
import com.brody.arxiv.work.summary.models.toWorkerModel
import com.brody.arxiv.work.summary.models.toWorkerState
import com.langdroid.core.LangDroidModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
internal class SummaryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    @ApplicationScope private val scope: CoroutineScope,
    @Dispatcher(ArxivDispatchers.Default) private val dispatcher: CoroutineDispatcher,
    private val workerStateConverter: WorkerStateConverter,
    private val workerSerializableConverter: WorkerSerializableConverter,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val saveSummaryUseCase: SaveSummaryUseCase,
    private val arxivNotificationManager: ArxivNotificationManager,
    private val settingsAiUseCase: SettingsAiUseCase,
    private val summarySettingsUseCase: GetSummarySettingsUseCase,
    private val sanityCheckUseCase: SanityCheckUseCase,
    private val getKeyUseCase: GetKeyUseCase,
    private val pdfTextExtractor: SimplePdfTextExtractor,
    private val pdfDownloadManager: PdfDownloadManager,
) : CoroutineWorker(context, parameters) {

    private lateinit var saveableModel: SaveablePaperDataModel
    private lateinit var prompts: SummaryWorkerPromptsInfo

    override suspend fun doWork(): Result {
        setupInputData()

        // 1. Load PDF
        updateState(SummaryWorkerState.PdfExtracting)
        val summaryPdfModel = saveableModel.toWorkerModel()
        val loadedPdf = pdfDownloadManager.getPdfFile(summaryPdfModel.pdfName)
        val extractedText = pdfTextExtractor.extract(loadedPdf)

        // 2. Create model, run summary chain and connect
        val languageModelSettings: SettingsAiDataModel = settingsAiUseCase.invoke().first()
        val selectedModel = languageModelSettings.selectedModel
        val selectedConfig = languageModelSettings.modelsConfig[selectedModel]
        val apiKey = getKeyUseCase(selectedModel).first()

        if (apiKey == null) {
            updateState(SummaryWorkerState.Failure.exception(NullApiKeyException()))
            return Result.failure()
        }
        if (!sanityCheckUseCase(selectedModel, apiKey)) {
            updateState(SummaryWorkerState.Failure.exception(ExpiredKeyException()))
            return Result.failure()
        }

        val model = LangDroidModel(
            selectedModel.toLangdroidModel(apiKey),
            selectedConfig?.toLangdroidConfig(),
        )

        val summaryLanguage = summarySettingsUseCase.invoke().firstOrNull()
            ?.summaryLanguage ?: SummaryLanguage.ENGLISH
        val summaryLanguageText = createLanguageText(summaryLanguage).orEmpty()

        val summaryChainFlow = generateSummaryUseCase(
            model = model,
            text = extractedText,
            isStream = selectedConfig?.isStream,
            chunkPrompt = prompts.chunkPrompt,
            finalPrompt = prompts.finalPrompt + summaryLanguageText,
            systemMessage = prompts.systemMessage
        )

//        Log.d("HELLOW", prompts.toString())


        val resultDeferred = CompletableDeferred<Boolean>()
        val summaryStringBuilder = StringBuilder()


        try {
            summaryChainFlow
                .map { state ->
                    // Convert state to SummaryWorkerState
                    state.toWorkerState().also { workerState ->
                        // Handle the output state specifically
                        if (workerState is SummaryWorkerState.Output) {
                            summaryStringBuilder.append(workerState.text)
                            workerState.text = summaryStringBuilder.toString()
                        }
                    }
                }.takeWhile { workerState ->
                    // Continue collecting until the state is Finished or Failure
                    when (workerState) {
                        is SummaryWorkerState.Finished -> {
                            resultDeferred.complete(true)
                            false // Stop collecting
                        }

                        is SummaryWorkerState.Failure -> {
                            resultDeferred.complete(false)
                            false // Stop collecting
                        }

                        else -> true // Keep collecting
                    }
                }
                .collect { workerState ->
                    // Update state for other cases, if needed
                    updateState(workerState)
                }
        } catch (e: CancellationException) {
            resultDeferred.completeExceptionally(e)
        } catch (e: Exception) {
            // Handle other exceptions, if necessary
            resultDeferred.complete(false)
        }

        // Await the result. This suspends until the deferred is completed, either with a success or failure result.
        val result = resultDeferred.await()

        // 4. Save result if ok
        return if (result) {
            updateState(SummaryWorkerState.Saving)

            val finalSummary = summaryStringBuilder.toString()

            saveSummaryUseCase.invoke(
                prompts.summaryType, finalSummary, saveableModel
            )


            updateState(SummaryWorkerState.Finished(finalSummary))
            Result.success()
        } else {
            updateState(SummaryWorkerState.Failure.exception(SummaryException()))
            Result.failure()
        }
    }


    private fun setupInputData() {
        saveableModel = workerSerializableConverter.deserialize(
            inputData.keyValueMap[MODEL_KEY] as String
        )
        prompts = workerSerializableConverter.deserialize(
            inputData.keyValueMap[SUMMARY_INFO_KEY] as String
        )
    }

    private suspend fun updateState(state: SummaryWorkerState) {
//        if (state !is SummaryWorkerState.Output)
//            Log.d("HELLOWINS", state::class.qualifiedName.toString())

        val stringState: String = workerStateConverter.serialize(state)

        if ((stringState.sizeInBytes + State.sizeInBytes) < 10240)
            setProgress(workDataOf(State to stringState))

        if (!isStopped) {
            try {
                val foregroundInfo = createForegroundInfo(state.toProcessingText(true))
                setForeground(foregroundInfo)
            } catch (_: IllegalStateException) {
            }
        }
    }

    private fun createForegroundInfo(processingText: String): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            arxivNotificationManager.createNotificationChannel()
        }

        val (notificationId, notification) = arxivNotificationManager.buildNotification(
            processingText, saveableModel, cancelIntent
        )

        return ForegroundInfo(notificationId, notification)
    }

    private val cancelIntent by lazy {
        WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
    }


    companion object {
        const val State = "State"

        const val MODEL_KEY = "model_key"
        const val SUMMARY_INFO_KEY = "summary_info_key"
    }
}