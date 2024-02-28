/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brody.arxiv.core.network.di

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.brody.arxiv.core.network.service.ChannelService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(15, TimeUnit.SECONDS)

      .apply {
        //TODO: MAKE FOR DEBUG AND RELEASE
//        if (BuildConfig.DEBUG) {
          this.addNetworkInterceptor(
            HttpLoggingInterceptor().apply {
              level = HttpLoggingInterceptor.Level.BODY
            }
          )
//        }
      }
      .build()
  }

  @Provides
  @Singleton
  fun okHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
    .addInterceptor(
      HttpLoggingInterceptor()
        .apply {
//          if (BuildConfig.DEBUG) {
            setLevel(HttpLoggingInterceptor.Level.BODY)
//          }
        },
    )
    .build()

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(
        "https://gist.githubusercontent.com/skydoves/a572b299a907753587818be56f3d3449/raw/" +
          "5c3e9a76e3848d83cc679a372c8b4875bb94b193/"
      )
      .addConverterFactory(MoshiConverterFactory.create().asLenient())
//      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun provideChannelService(retrofit: Retrofit): ChannelService = retrofit.create()

  @Provides
  @Singleton
  fun imageLoader(
    okHttpCallFactory: Call.Factory,
    @ApplicationContext application: Context,
  ): ImageLoader = ImageLoader.Builder(application)
    .callFactory(okHttpCallFactory)
    .components {
      add(SvgDecoder.Factory())
    }
    // Assume most content images are versioned urls
    // but some problematic images are fetching each time
    .respectCacheHeaders(false)
    .apply {
      //TODO
//      if (BuildConfig.DEBUG) {
        logger(DebugLogger())
//      }
    }
    .build()
}
