package ru.netology.nmedia.application

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.IPostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.work.RefreshPostsWorker
import java.util.concurrent.TimeUnit

class NMediaApplication : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)
    companion object {
        lateinit var repository: IPostRepository
        lateinit var appAuth: AppAuth
    }

    override fun onCreate() {
        super.onCreate()
        setupAuth()
        setupWork()
        repository = PostRepositoryImpl(AppDb.getInstance(applicationContext).postDao())
        appAuth = AppAuth.getInstance()
    }

    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(this@NMediaApplication).enqueueUniquePeriodicWork(
                RefreshPostsWorker.name,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    private fun setupAuth() {
        appScope.launch {
            AppAuth.initApp(this@NMediaApplication)
        }
    }
}