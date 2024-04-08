package com.brody.arxiv.features.details.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.pdf.download.PdfDownloadManager
import com.brody.arxiv.core.pdf.download.PdfDownloadState
import com.brody.arxiv.core.pdf.download.utils.openPdfWithViewer
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.features.details.presentation.models.DetailsUiModel
import com.brody.arxiv.features.details.presentation.models.toPresentationModel
import com.brody.arxiv.features.details.presentation.models.toSaveableModel
import com.brody.arxiv.shared.saved.domain.usecases.ToggleSaveItemUseCase
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val toggleSaveItemUseCase: ToggleSaveItemUseCase,
    private val pdfDownloadManager: PdfDownloadManager,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val currentItem: MutableStateFlow<DetailsUiModel?> = MutableStateFlow(null)

    val uiState: StateFlow<DetailsUiState> = currentItem.map {
        if (it == null) DetailsUiState.Waiting
        else DetailsUiState.Connected(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DetailsUiState.Waiting,
    )

    private lateinit var downloadState: StateFlow<DownloadUiState>

    init {
        downloadState = merge(
            pdfDownloadManager.downloadState.map { DownloadMergeFlowState.DownloadState(it) },
            currentItem.map { DownloadMergeFlowState.Item(it) }
        ).map { flowState ->
            when (flowState) {
                is DownloadMergeFlowState.Item -> {
                    val item = flowState.item
                    if (item == null) DownloadUiState.Ignore
                    else {
                        // Check if there is file present
                        if (!pdfDownloadManager.isPdfCorrupted(item.pdfName)) {
                            DownloadUiState.ReadyToRead(item.pdfName)
                        } else DownloadUiState.ReadyToLoad
                    }
                }

                is DownloadMergeFlowState.DownloadState -> {
                    val state = flowState.state
                    if (state is PdfDownloadState.Idle)
                        return@map DownloadUiState.ReadyToLoad

                    val itemPdfName =
                        currentItem.value?.pdfName ?: return@map DownloadUiState.Ignore
                    val responsePdfName = state.downloadFileName

                    if (!responsePdfName.contains(itemPdfName, true))
                        return@map DownloadUiState.Ignore

                    when (state) {
                        is PdfDownloadState.Downloading -> {
                            if (state.progress == 1.0f) {
                                val isCorrupted = pdfDownloadManager.isPdfCorrupted(responsePdfName)
                                if (isCorrupted) DownloadUiState.Downloading(
                                    state.progress, responsePdfName
                                ) else DownloadUiState.ReadyToRead(responsePdfName)
                            } else DownloadUiState.Downloading(
                                state.progress, responsePdfName
                            )
                        }

                        is PdfDownloadState.Done -> {
                            // Check for corruption
                            val isCorrupted = pdfDownloadManager.isPdfCorrupted(responsePdfName)
                            if (isCorrupted)
                                DownloadUiState.Failed(
                                    "Something wrong with PDF file, " +
                                            "check it on Arxiv page directly"
                                )
                            else DownloadUiState.ReadyToRead(responsePdfName)
                        }

                        is PdfDownloadState.Failed -> DownloadUiState.Failed()
                        else -> DownloadUiState.ReadyToLoad
                    }
                }

                else -> DownloadUiState.Ignore
            }
        }.map {
            if (it is DownloadUiState.Ignore && ::downloadState.isInitialized)
                downloadState.value
            else it
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            DownloadUiState.ReadyToLoad
        )

    }

    val onDownloadState: () -> StateFlow<DownloadUiState>
        get() = { downloadState }

    fun saveItem(id: String, item: DetailsUiModel) {
        viewModelScope.launch(coroutineDispatcher) {
            toggleSaveItemUseCase.invoke(
                id, if (item.isSaved && !item.hasSummaries) null
                else item.toSaveableModel()
            )
            currentItem.update {
                it?.copy(isSaved = !item.isSaved)
            }
        }
    }

    fun connectDetailsItem(model: SaveablePaperDataModel) {
        currentItem.update { model.toPresentationModel() }
    }

    fun downloadPdf(url: String, fileName: String) {
        viewModelScope.launch {
            pdfDownloadManager.startDownloading(url, fileName)
        }
    }

    fun openPdfByName(context: Context, fileName: String) {
        val uri = pdfDownloadManager.getPdfUri(fileName)
        openPdfWithViewer(context, uri)
    }
}

sealed interface DetailsUiState {
    data object Waiting : DetailsUiState
    data class Connected(
        val model: DetailsUiModel
    ) : DetailsUiState
}


/*
1. Map from currentItem, check if the same pdf downloaded as page opened (by name after last slash)
 - if not -> Idle, if yes - Continue by mapping to same
2. Check if Done() -> make Checking() and do pdf corruption check
3. if ok -> Done() here
*/

@Immutable
sealed interface DownloadMergeFlowState {
    data class Item(val item: DetailsUiModel?)
    data class DownloadState(val state: PdfDownloadState)
}

@Immutable
sealed interface DownloadUiState {

    data object Ignore : DownloadUiState
    data object ReadyToLoad : DownloadUiState
    data class Downloading(val progress: Float, val fileName: String) : DownloadUiState
//    data class Checking(val downloadFileName: String) : DownloadUiState

    data class Failed(val message: String = "Something went wrong with PDF download") :
        DownloadUiState

    data class ReadyToRead(val fileName: String) : DownloadUiState
}
