package ru.netology.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.repository.IPostRepository

class RemovePostWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: IPostRepository
) : CoroutineWorker(applicationContext, params) {

    companion object {
        const val postKey = "post"
        const val name = "ru.netology.nmedia.work.RemovePostWorker"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(SavePostWorker.postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        return try {
            repository.removePost(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}