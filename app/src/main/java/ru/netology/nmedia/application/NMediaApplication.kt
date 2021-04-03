package ru.netology.nmedia.application

import android.app.Application
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.IPostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class NMediaApplication : Application() {
    companion object {
        lateinit var repository: IPostRepository
        lateinit var appAuth: AppAuth
    }

    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
        repository = PostRepositoryImpl(AppDb.getInstance(applicationContext).postDao())
        appAuth = AppAuth.getInstance()
    }
}