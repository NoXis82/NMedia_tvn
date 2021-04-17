package ru.netology.nmedia.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.IPostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.work.RefreshPostsWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class NMediaApplication : Application(), Configuration.Provider {
    private val appScope = CoroutineScope(Dispatchers.Default)


    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        setupWork()
    }

    private fun setupAuth() {
        appScope.launch {
            appAuth.sendPushToken()
        }
    }

    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(
                RefreshPostsWorker.name,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}