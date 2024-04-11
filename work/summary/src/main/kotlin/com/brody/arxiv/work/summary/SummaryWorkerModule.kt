package com.brody.arxiv.work.summary

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.WorkManager
import com.brody.arxiv.work.summary.converters.WorkerSerializableConverter
import com.brody.arxiv.work.summary.converters.WorkerStateConverter
import com.brody.arxiv.work.summary.manager.SummaryWorkerManager
import com.brody.arxiv.work.summary.manager.SummaryWorkerManagerImpl
import com.brody.arxiv.work.summary.models.SummaryWorkerState
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class SummaryWorkerModule {

    @Binds
    abstract fun bindWorkManager(workerManager: SummaryWorkerManagerImpl): SummaryWorkerManager

    companion object {
        @Provides
        @Singleton
        fun provideWorkerConverter(): WorkerStateConverter {
            val module = SerializersModule {
                polymorphic(SummaryWorkerState::class) {
                    subclass(
                        SummaryWorkerState.Connecting::class,
                        SummaryWorkerState.Connecting.serializer()
                    )
                    subclass(
                        SummaryWorkerState.PdfExtracting::class,
                        SummaryWorkerState.PdfExtracting.serializer()
                    )
                    subclass(
                        SummaryWorkerState.TextSplitting::class,
                        SummaryWorkerState.TextSplitting.serializer()
                    )
                    subclass(
                        SummaryWorkerState.Saving::class,
                        SummaryWorkerState.Saving.serializer()
                    )
                    subclass(
                        SummaryWorkerState.Finished::class,
                        SummaryWorkerState.Finished.serializer()
                    )
                    subclass(
                        SummaryWorkerState.Reduce::class,
                        SummaryWorkerState.Reduce.serializer()
                    )
                    subclass(
                        SummaryWorkerState.Output::class,
                        SummaryWorkerState.Output.serializer()
                    )
                    subclass(
                        SummaryWorkerState.Summarizing::class,
                        SummaryWorkerState.Summarizing.serializer()
                    )
                    subclass(
                        SummaryWorkerState.Failure::class,
                        SummaryWorkerState.Failure.serializer()
                    )
                }
            }
            val json = Json { serializersModule = module }
            return WorkerStateConverter(json)
        }

        @Provides
        @Singleton
        fun provideSerializableConverter(): WorkerSerializableConverter {
            val json = Json

            return WorkerSerializableConverter(json)
        }
    }
}
