package com.brody.arxiv.shared.papers.data.remote.source

import com.brody.arxiv.shared.papers.models.data.ArxivResponse

internal interface PapersRemoteDataSource {
    suspend fun getPapers(query: String): ArxivResponse
}