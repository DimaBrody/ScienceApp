package com.brody.arxiv.work.summary.manager

import androidx.work.workDataOf
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.work.summary.SummaryWorker
import com.brody.arxiv.work.summary.models.SummaryWorkerPromptsInfo
import com.brody.arxiv.work.summary.models.SummaryWorkerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface SummaryWorkerManager {

    fun isRunning(id: String): Flow<Boolean>

    fun workerFlow(id: String): Flow<SummaryWorkerState>

    fun startSummaryWork(
        saveableModel: SaveablePaperDataModel,
        summaryWorkerPromptsInfo: SummaryWorkerPromptsInfo
    )

//    fun tryToConnectToWorker(id: String)

    fun cancelWork(id: String)
}