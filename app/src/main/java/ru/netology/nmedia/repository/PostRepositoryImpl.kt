package ru.netology.nmedia.repository


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ApiError

import java.lang.RuntimeException

class PostRepositoryImpl : IPostRepository {

    override fun getAllAsync(callback: IPostRepository.GetAllCallback) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    callback.onSuccess(response.body().orEmpty())
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(ApiError.fromThrowable(t))
                }
            })
    }

    override fun unLikeById(id: Long, callback: IPostRepository.LikeByIdCallback) {
        PostsApi.retrofitService.unLikeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    callback.onSuccess(
                        response.body()
                            ?: throw RuntimeException(R.string.body_empty.toString())
                    )
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(ApiError.fromThrowable(t))
                }
            })
    }

    override fun likeById(id: Long, callback: IPostRepository.LikeByIdCallback) {
        PostsApi.retrofitService.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    callback.onSuccess(
                        response.body()
                            ?: throw RuntimeException(R.string.body_empty.toString())
                    )
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(ApiError.fromThrowable(t))
                }
            })
    }

    override fun removePost(id: Long, callback: IPostRepository.RemovePostCallback) {
        PostsApi.retrofitService.removePost(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(ApiError.fromThrowable(t))
                }
            })
    }

    override fun savePost(post: Post, callback: IPostRepository.SavePostCallback) {
        PostsApi.retrofitService.savePost(post)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    callback.onSuccess(
                        response.body()
                            ?: throw RuntimeException(R.string.body_empty.toString())
                    )
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(ApiError.fromThrowable(t))
                }
            })
    }
}