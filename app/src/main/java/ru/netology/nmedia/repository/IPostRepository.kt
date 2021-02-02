package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.ApiError


interface IPostRepository {

    fun getAllAsync(callback: GetAllCallback)
    fun unLikeById(id: Long, callback: LikeByIdCallback)
    fun likeById(id: Long, callback: LikeByIdCallback)
    fun removePost(id: Long, callback: RemovePostCallback)
    fun savePost(post: Post, callback: SavePostCallback)


    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: ApiError)
    }

    interface LikeByIdCallback {
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }

    interface RemovePostCallback {
        fun onSuccess()
        fun onError(e: Exception)
    }

    interface SavePostCallback {
        fun onSuccess(post: Post)
        fun onError(e: ApiError)
    }

}