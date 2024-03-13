package com.brody.arxiv.shared.papers.data.remote.source

import com.brody.arxiv.shared.papers.data.remote.api.PapersApi
import com.brody.arxiv.shared.papers.models.data.ArxivResponse
import retrofit2.Response
import javax.inject.Inject

internal class PapersRemoteDataSourceImpl @Inject constructor(
    private val papersApi: PapersApi
) : PapersRemoteDataSource {

    override suspend fun getPapers(query: String): ArxivResponse {
        return papersApi.getPapers(query)
    }
}