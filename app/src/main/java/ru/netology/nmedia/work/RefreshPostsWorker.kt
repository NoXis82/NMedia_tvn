package ru.netology.nmedia.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.repository.IPostRepository

@HiltWorker
class RefreshPostsWorker @AssistedInject  constructor (
    @Assisted applicationContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: IPostRepository
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val name = "ru.netology.nmedia.work.RefreshPostsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getAll()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}