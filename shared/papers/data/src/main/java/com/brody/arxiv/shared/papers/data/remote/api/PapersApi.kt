package com.brody.arxiv.shared.papers.data.remote.api

import com.brody.arxiv.shared.papers.models.data.ArxivResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

internal interface PapersApi {
    @GET
    suspend fun getPapers(@Url url: String): ArxivResponse
}