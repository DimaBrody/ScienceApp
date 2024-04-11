package com.brody.arxiv.shared.summary.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.brody.arxiv.core.threading.ApplicationScope
import com.brody.arxiv.shared.summary.data.SummaryPreferences
import com.brody.arxiv.shared.summary.data.database.SummaryDao
import com.brody.arxiv.shared.summary.data.database.SummaryDatabase
import com.brody.arxiv.shared.summary.data.serializers.SummaryPreferencesSerializer
import com.brody.arxiv.shared.summary.data.source.SummaryDataSource
import com.brody.arxiv.shared.summary.data.source.SummaryDataSourceImpl
import com.brody.arxiv.shared.summary.data.repository.SummaryRepositoryImpl
import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.langdroid.core.LangDroidModel
import com.langdroid.summary.SummaryChain
import com.langdroid.summary.prompts.PromptTemplate
import com.langdroid.summary.prompts.PromptsAndMessage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SummaryHiltModule {

    @Binds
    fun bindSummaryRepository(summaryRepository: SummaryRepositoryImpl): SummaryRepository

    @Binds
    fun bindSummaryDataSource(summaryDataSourceImpl: SummaryDataSourceImpl): SummaryDataSource


    companion object {
//        @Provides
//        fun provideSubscriptionApi(retrofit: Retrofit): SubscriptionApi = retrofit.create(SubscriptionApi::class.java)

        @Provides
        @Singleton
        internal fun providesQueryPreferencesDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            summaryPreferencesSerializer: SummaryPreferencesSerializer,
        ): DataStore<SummaryPreferences> =
            DataStoreFactory.create(
                serializer = summaryPreferencesSerializer,
                scope = scope,
            ) {
                context.dataStoreFile("summary_preferences.pb")
            }

        @Singleton
        @Provides
        fun provideSummaryDatabase(@ApplicationContext context: Context): SummaryDatabase =
            Room
                .databaseBuilder(context, SummaryDatabase::class.java, "summaries_database")
                .build()

        @Provides
        fun provideSummaryDao(summaryDatabase: SummaryDatabase): SummaryDao =
            summaryDatabase.papersDao()

        @Provides
        @Singleton
        fun provideSummaryChainFactory(): SummaryChainFactory {
            return object : SummaryChainFactory {
                override fun create(
                    model: LangDroidModel<*>,
                    isStream: Boolean,
                    chunkPrompt: String?,
                    finalPrompt: String?,
                    systemMessage: String?
                ): SummaryChain<*> {
                    return SummaryChain(
                        model = model,
                        isStream = isStream,
                        promptsAndMessage = PromptsAndMessage(
                            chunkPrompt, finalPrompt, systemMessage
                        )
                    )
                }
            }
        }
    }


}