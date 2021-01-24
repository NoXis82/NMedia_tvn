package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.*

interface IPostRepository {

    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun share(id: Long)
    fun removePost(id: Long)
    fun savePost(post: Post)

}