package ru.netology.nmedia.repository


import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : IPostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()
        return client.newCall(request)
            .execute()
            .use { it.body?.string() }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun likeById(id: Long) {
        val requestGetPost: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id")
            .build()
        val post = client.newCall(requestGetPost)
            .execute()
            .use { it.body?.string() }
            .let {
                gson.fromJson(it, Post::class.java)
            }
        val requestLike: Request = if (post.likedByMe) {
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        }
        client.newCall(requestLike)
            .execute()
            .close()
    }

    override fun share(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removePost(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun savePost(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}