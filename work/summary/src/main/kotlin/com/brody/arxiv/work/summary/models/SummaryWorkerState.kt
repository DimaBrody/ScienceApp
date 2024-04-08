package com.brody.arxiv.work.summary.models

import com.brody.summary.SummaryState
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
sealed interface SummaryWorkerState {
    @Serializable
    data object Connecting : SummaryWorkerState

    @Serializable
    data object PdfExtracting : SummaryWorkerState

    @Serializable
    data object TextSplitting : SummaryWorkerState

    @Serializable
    data object Saving : SummaryWorkerState

    @Serializable
    data class Reduce(val processedChunks: Int, val allChunks: Int) : SummaryWorkerState

    @Serializable
    data class Output(var text: String) : SummaryWorkerState

    @Serializable
    data class Finished(var finalSummary: String? = null) : SummaryWorkerState

    @Serializable
    data class Failure(val exceptionInfo: ExceptionInfo) : SummaryWorkerState {
        companion object {
            fun exception(t: Throwable) = Failure(t.toExceptionInfo())
        }
    }
}

@Serializable
data class ExceptionInfo(
    val className: String,
    val message: String?
)

fun Throwable.toExceptionInfo(): ExceptionInfo =
    ExceptionInfo(this::class.qualifiedName ?: "Unknown", this.message)


fun SummaryWorkerState.toProcessingText(isNotification: Boolean): String = when (this) {
    is SummaryWorkerState.Connecting -> if (isNotification) "Connecting..." else "Here will be your summary..."
    is SummaryWorkerState.PdfExtracting -> "Extracting PDF content..."
    is SummaryWorkerState.TextSplitting -> "Splitting text..."
    is SummaryWorkerState.Saving -> "Saving summary..."
    is SummaryWorkerState.Reduce -> "Processing chunks $processedChunks/$allChunks"
    is SummaryWorkerState.Output -> if (isNotification) "Printing summary..." else text
    is SummaryWorkerState.Failure -> "Failed: ${exceptionInfo.message.orEmpty()}"
    is SummaryWorkerState.Finished ->
        if (isNotification || finalSummary.isNullOrEmpty()) "Summary is finished" else finalSummary!!
}

fun SummaryState.toWorkerState(): SummaryWorkerState = when (this) {
    is SummaryState.Idle -> SummaryWorkerState.Connecting // Assuming Idle corresponds to Connecting
    is SummaryState.TextSplitting -> SummaryWorkerState.TextSplitting
    is SummaryState.Reduce -> SummaryWorkerState.Reduce(processedChunks, allChunks)
    is SummaryState.Output -> SummaryWorkerState.Output(text)
    is SummaryState.Finished -> SummaryWorkerState.Finished()
    is SummaryState.Failure -> SummaryWorkerState.Failure(t.toExceptionInfo())
}


