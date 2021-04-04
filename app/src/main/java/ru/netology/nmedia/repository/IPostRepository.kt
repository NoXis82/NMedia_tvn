package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.*

interface IPostRepository {
    val posts: Flow<List<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    fun getNewerList(id: Long): Flow<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun unLikeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun removePost(id: Long): Unit
    suspend fun savePost(post: PostEntity): Long
    suspend fun sendPost(post: Post): Post
    suspend fun sendNewer(posts: List<Post>)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun updateUser(login: String, pass: String): AuthState
}