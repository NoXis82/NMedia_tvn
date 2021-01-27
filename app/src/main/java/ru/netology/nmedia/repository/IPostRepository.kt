package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.*

interface IPostRepository {

//    fun getAll()
    fun unLikeById(id: Long, callback: LikeByIdCallback)
    fun likeById(post: Post, callback: LikeByIdCallback)
    fun share(id: Long)
    fun removePost(id: Long)
    fun savePost(post: Post)


    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }

    interface LikeByIdCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }

}