package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.ApiError


interface IPostRepository {
    val posts: Flow<List<Post>>
    fun getNewerCount(id: Long) : Flow<Int>
    fun getNewerList(id: Long) : Flow<List<Post>>

    suspend fun getAll(): List<Post>
    suspend fun unLikeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun removePost(id: Long): Unit
    suspend fun savePost(post: PostEntity): Long
    suspend fun sendPost(post: Post): Post
    suspend fun sendNewer(posts: List<Post>)
    suspend fun count() : Int
}