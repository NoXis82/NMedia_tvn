package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import java.util.concurrent.TimeUnit

interface PostApiService {
    @GET("posts")
    suspend fun getAll(): List<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unLikeById(@Path("id") id: Long): Post

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Post

    @DELETE("posts/{id}")
    suspend fun removePost(@Path("id") id: Long): Unit

    @POST("posts")
    suspend fun savePost(@Body post: Post): Post

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): List<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Media

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): AuthState

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun regUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): AuthState

    @POST("users/push-tokens")
    suspend fun push(@Body pushToken: PushToken): Unit

}
