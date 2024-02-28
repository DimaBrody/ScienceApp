//package com.brody.arxiv.onboarding.data.remote.source
//
//import com.brody.core.common.response.Response
//import com.brody.arxiv.subjects.models.remote.SubscriptionRemoteModel
//import com.brody.arxiv.subjects.models.remote.UpdateAutoRenewalBodyRemoteModel
//
//internal interface SubscriptionRemoteDataSource {
//    suspend fun getSubscription(subscriptionId: Int): Response<SubscriptionRemoteModel>
//    suspend fun updateAutoRenewal(body: UpdateAutoRenewalBodyRemoteModel): Response<Unit>
//}