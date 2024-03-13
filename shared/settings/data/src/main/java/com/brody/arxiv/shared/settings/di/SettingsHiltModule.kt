package com.brody.arxiv.shared.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.brody.arxiv.core.threading.ApplicationScope
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.settings.data.QueryPreferences
import com.brody.arxiv.shared.settings.serializers.QueryPreferencesSerializer
import com.brody.arxiv.shared.settings.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.repository.SettingsRepositoryImpl
import com.brody.arxiv.shared.settings.source.SettingsDataSource
import com.brody.arxiv.shared.settings.source.SettingsDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SettingsHiltModule {

    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindSettingsDataSource(settingsDataSourceImpl: SettingsDataSourceImpl): SettingsDataSource


    companion object {
//        @Provides
//        fun provideSubscriptionApi(retrofit: Retrofit): SubscriptionApi = retrofit.create(SubscriptionApi::class.java)

        @Provides
        @Singleton
        internal fun providesQueryPreferencesDataStore(
            @ApplicationContext context: Context,
            @Dispatcher(ArxivDispatchers.IO) ioDispatcher: CoroutineDispatcher,
            @ApplicationScope scope: CoroutineScope,
            queryPreferencesSerializer: QueryPreferencesSerializer,
        ): DataStore<QueryPreferences> =
            DataStoreFactory.create(
                serializer = queryPreferencesSerializer,
                scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            ) {
                context.dataStoreFile("query_preferences.pb")
            }
    }


}