package com.brody.arxiv.work.summary.manager

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.work.summary.SummaryWorker
import com.brody.arxiv.work.summary.converters.WorkerSerializableConverter
import com.brody.arxiv.work.summary.converters.WorkerStateConverter
import com.brody.arxiv.work.summary.delegating.DelegatingWorker
import com.brody.arxiv.work.summary.delegating.WORKER_CLASS_NAME
import com.brody.arxiv.work.summary.delegating.delegatedData
import com.brody.arxiv.work.summary.models.SummaryWorkerPromptsInfo
import com.brody.arxiv.work.summary.models.SummaryWorkerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

internal class SummaryWorkerManagerImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val workerSerializableConverter: WorkerSerializableConverter,
    private val workerStateConverter: WorkerStateConverter,
) : SummaryWorkerManager {

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

    override fun startSummaryWork(
        saveableModel: SaveablePaperDataModel, summaryWorkerPromptsInfo: SummaryWorkerPromptsInfo
    ) {
        val inputData = workDataOf(
            WORKER_CLASS_NAME to SummaryWorker::class.qualifiedName,
            SummaryWorker.MODEL_KEY to workerSerializableConverter.serialize(saveableModel),
            SummaryWorker.SUMMARY_INFO_KEY to workerSerializableConverter.serialize(
                summaryWorkerPromptsInfo
            )
        )

        val workerId = saveableModel.id
        val workRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setInputData(inputData)
                .addTag(workerId)
                .build()


        workManager.enqueueUniqueWork(workerId, ExistingWorkPolicy.REPLACE, workRequest)
//        observeWorkProgress(workRequest.id)
    }

//    override fun tryToConnectToWorker(id: String) {
//        workManager.getWorkInfosForUniqueWorkLiveData(id).observeForever { workInfoList ->
//            val workInfo = workInfoList.firstOrNull()
//
//            if (workInfo != null && (workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED)) {
//                observeWorkProgress(workInfo.id)
//            }
//        }
//    }

    override fun workerFlow(id: String): Flow<SummaryWorkerState> =
        workManager.getWorkInfosForUniqueWorkFlow(id).map { workInfos ->
            val workInfo = workInfos.firstOrNull()

            if (workInfo != null && workInfo.state == WorkInfo.State.RUNNING) {
                val progress = workInfo.progress
                val workerStateString = progress.getString(SummaryWorker.State)
                    ?: return@map SummaryWorkerState.Connecting

                val workerState = workerStateConverter.deserialize(workerStateString)

                workerState
            } else SummaryWorkerState.Connecting
        }
//
//    private fun observeWorkProgress(workId: UUID) {
//        workManager.getWorkInfoByIdLiveData(workId).observeForever { workInfo ->
//            if (workInfo != null && workInfo.state == WorkInfo.State.RUNNING) {
//                val progress = workInfo.progress
//                val workerStateString = progress.keyValueMap[SummaryWorker.State] as String
//                val workerState = workerStateConverter.deserialize(workerStateString)
//
//                workerFlow.tryEmit(workerState)
//            }
//        }
//    }

    override fun isRunning(id: String): Flow<Boolean> =
        workManager.getWorkInfosForUniqueWorkFlow(id)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    override fun cancelWork(id: String) {
        workManager.cancelUniqueWork(id)
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }