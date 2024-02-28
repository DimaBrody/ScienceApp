//package com.brody.arxiv.onboarding.data.remote.source
//
//import com.brody.core.common.response.Response
//import com.brody.core.network.utils.executeRequest
//import com.brody.core.network.utils.executeRequestNullable
//import com.brody.arxiv.onboarding.data.remote.api.SubscriptionApi
//import com.brody.arxiv.subjects.models.remote.SubscriptionRemoteModel
//import com.brody.arxiv.subjects.models.remote.UpdateAutoRenewalBodyRemoteModel
//import javax.inject.Inject
//
//internal class SubscriptionRemoteDataSourceImpl @Inject constructor(
//    private val subscriptionApi: SubscriptionApi
//) : SubscriptionRemoteDataSource {
//    override suspend fun getSubscription(subscriptionId: Int): Response<SubscriptionRemoteModel> = executeRequest {
//        subscriptionApi.getSubscription(subscriptionId)
//    }
//
//    override suspend fun updateAutoRenewal(body: UpdateAutoRenewalBodyRemoteModel): Response<Unit> =
//        executeRequestNullable {
//            subscriptionApi.updateAutoRenewal(body)
//        }
//}