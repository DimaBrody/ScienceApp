package com.brody.arxiv.shared.settings.ai.data.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.brody.arxiv.core.threading.ApplicationScope
import com.brody.arxiv.shared.settings.ai.data.AiPreferences
import com.brody.arxiv.shared.settings.ai.data.serializers.AiPreferencesSerializer
import com.brody.arxiv.shared.settings.ai.data.serializers.SecuredPreferencesSerializer
import com.brody.arxiv.shared.settings.ai.data.repository.SettingsAiRepositoryImpl
import com.brody.arxiv.shared.settings.ai.data.source.SettingsAiDataSource
import com.brody.arxiv.shared.settings.ai.data.source.SettingsAiDataSourceImpl
import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import com.brody.arxiv.shared.settings.ai.models.data.ModelKeys
import com.brody.arxiv.shared.settings.ai.models.data.toLangdroidModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.langdroid.core.LangDroidModel
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SettingsAiHiltModule {

    @Binds
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsAiRepositoryImpl): SettingsAiRepository

    @Binds
    fun bindSettingsDataSource(settingsDataSourceImpl: SettingsAiDataSourceImpl): SettingsAiDataSource


    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_prefs"
        private const val MASTER_KEY_URI = "android-keystore://master_key"

        private const val DATASTORE_FILE = "model.pb"

//        @Provides
//        fun provideSubscriptionApi(retrofit: Retrofit): SubscriptionApi = retrofit.create(SubscriptionApi::class.java)

        @Provides
        @Singleton
        internal fun providesAiPreferencesDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            aiPreferencesSerializer: AiPreferencesSerializer,
        ): DataStore<AiPreferences> =
            DataStoreFactory.create(
                serializer = aiPreferencesSerializer,
                scope = scope,
            ) {
                context.dataStoreFile("ai_preferences.pb")
            }

        @Provides
        @Singleton
        fun provideAead(application: Application): Aead {
            AeadConfig.register()

            return AndroidKeysetManager.Builder()
                .withSharedPref(application, KEYSET_NAME, PREFERENCE_FILE)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)
        }

        @Provides
        @Singleton
        fun provideSecuredDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            securedPreferencesSerializer: SecuredPreferencesSerializer
        ): DataStore<ModelKeys> {
            return DataStoreFactory.create(
                produceFile = { File(context.filesDir, "datastore/$DATASTORE_FILE") },
                serializer = securedPreferencesSerializer,
                scope = scope
            )
        }

        @Provides
        @Singleton
        fun provideLangDroidModelFactory(): LangDroidModelFactory {
            return object : LangDroidModelFactory {
                override fun create(model: LanguageModel, key: String): LangDroidModel<*> {
                    // Implement factory logic to create LangDroidModel instances
                    // This is just a placeholder. Replace it with actual implementation.
                    return LangDroidModel(model.toLangdroidModel(key))
                }
            }
        }


    }


}