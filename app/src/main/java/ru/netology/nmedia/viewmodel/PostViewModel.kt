package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread


class PostViewModel(application: Application) : AndroidViewModel(application) {

    var isHandledBackPressed: String = ""

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        published = "",
        videoUrl = ""
    )
    private val repository: IPostRepository = PostRepositoryImpl()
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postsRefreshError = SingleLiveEvent<Unit>()
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun like(post: Post) {
        if (post.likedByMe) {
            repository.unLikeById(post.id, object : IPostRepository.LikeByIdCallback {
                override fun onSuccess(post: Post) {
                    _state.postValue(
                        FeedModel(posts = _state.value?.posts.orEmpty().map {
                            if (it.id != post.id) it else it.copy(
                                likes = post.likes,
                                likedByMe = post.likedByMe,
                                videoUrl = "test"
                            )
                        })
                    )
                }

                override fun onError(e: Exception) {
                    _state.value = FeedModel(error = true)
                }
            })
        } else {
            repository.likeById(post, object : IPostRepository.LikeByIdCallback {
                override fun onSuccess(post: Post) {
                    _state.postValue(
                        FeedModel(posts = _state.value?.posts.orEmpty().map {
                            if (it.id != post.id) it else it.copy(
                                likes = post.likes,
                                likedByMe = post.likedByMe,
                                videoUrl = "test"
                            )
                        })
                    )
                }

                override fun onError(e: Exception) {
                    _state.value = FeedModel(error = true)
                }
            })
        }
    }

    fun share(id: Long) = repository.share(id)

    fun removePost(id: Long) {
        thread {
            val old = _state.value?.posts.orEmpty()
            _state.postValue(
                _state.value?.copy(posts = _state.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removePost(id)
            } catch (e: IOException) {
                _state.postValue(_state.value?.copy(posts = old))
            }
        }
    }

    fun refreshingPosts() {
        val old = _state.value?.posts.orEmpty()
        _state.value = FeedModel(refreshing = true)
        repository.getAllAsync(object : IPostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _state.postValue(FeedModel(posts = posts))
            }

            override fun onError(e: Exception) {
                _state.value?.copy(refreshing = false)
                _state.postValue(FeedModel(posts = old))
                _postsRefreshError.postValue(Unit)
            }

        })
    }

    fun loadPosts() {
        _state.value = FeedModel(loading = true)
        repository.getAllAsync(object : IPostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _state.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _state.postValue(FeedModel(error = true))
            }
        })
    }

    fun savePost() {
        edited.value?.let {
            thread {
                repository.savePost(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun editContent(post: Post) {
        edited.value = post
    }

}