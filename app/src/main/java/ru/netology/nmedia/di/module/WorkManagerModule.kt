package ru.netology.nmedia.di.module

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.di.module.factory.DependencyWorkerFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WorkManagerModule {

    @Provides
    @Singleton
    fun providesWorkManager(
        @ApplicationContext context: Context,
        workerFactory: DependencyWorkerFactory
    ): WorkManager {
        WorkManager.initialize(
            context, Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
        return WorkManager.getInstance(context)
    }
}