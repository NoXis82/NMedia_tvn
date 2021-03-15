package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.ApiError


interface IPostRepository {
    val posts: LiveData<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun unLikeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun removePost(id: Long): Unit
    suspend fun savePost(post: PostEntity): Long
    suspend fun sendPost(post: Post): Post
}