package ru.netology.nmedia.api

import okhttp3.Interceptor
import okhttp3.Response
import ru.netology.nmedia.model.AppError

class PostsInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request())
            .let { response ->
                when {
                    response.isSuccessful -> response
                    else -> throw AppError(response.code, response.message)
                }
            }
}