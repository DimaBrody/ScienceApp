//package com.brody.arxiv.onboarding.data.remote.api
//
//import com.brody.arxiv.subjects.models.remote.SubscriptionRemoteModel
//import com.brody.arxiv.subjects.models.remote.UpdateAutoRenewalBodyRemoteModel
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Path
//
//internal interface SubscriptionApi {
//    @POST("/api/v1/subscriptions/{subscription_id}/mono_bank_subscription_payment_intents")
//    suspend fun getSubscription(@Path("subscription_id") subscriptionId: Int): Response<SubscriptionRemoteModel>
//
//    @PUT("/api/v1/android/subscriptions")
//    suspend fun updateAutoRenewal(@Body body: UpdateAutoRenewalBodyRemoteModel): Response<Unit>
//}