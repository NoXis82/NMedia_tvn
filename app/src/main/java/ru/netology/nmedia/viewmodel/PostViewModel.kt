package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.PostState
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.File
import java.io.IOException

class PostViewModel(application: Application) : AndroidViewModel(application) {

    var isHandledBackPressed: String = ""
    private val empty = Post(
        content = "",
        author = "Student",
        authorAvatar = "nelotogy.jpg",
        published = ""
    )
    private val noPhoto = PhotoModel()
    private var localId = 0L
    private val repository: IPostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val posts: LiveData<List<Post>>
        get() = repository.posts.asLiveData(Dispatchers.Default)

    private val _postsRefreshError = SingleLiveEvent<Unit>()
    val postsRefreshError: LiveData<Unit>
        get() = _postsRefreshError

    private val _postRemoveError = SingleLiveEvent<Unit>()
    val postRemoveError: LiveData<Unit>
        get() = _postRemoveError

    private val _postLikeError = SingleLiveEvent<Unit>()
    val postLikeError: LiveData<Unit>
        get() = _postLikeError

    val newPosts = posts.switchMap {
        repository.getNewerCount(it.firstOrNull()?.id ?: 0L)
            .asLiveData()
    }

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun checkNewPost(count: Int) {
        if (count > 0) {
            _state.value = FeedModel(visibleFab = true)
        }
    }

    fun getNewerPosts() {
        viewModelScope.launch {
            try {
                val newerPosts = repository.getNewerList(posts.value?.firstOrNull()?.id ?: 0L)
                newerPosts.collect { posts ->
                    repository.sendNewer(posts)
                }
                _state.value = FeedModel(visibleFab = false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun like(post: Post) {
        if (post.likedByMe) {
            viewModelScope.launch {
                try {
                    repository.unLikeById(post.id)
                } catch (e: IOException) {
                    _postLikeError.value = Unit
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    repository.likeById(post.id)
                } catch (e: IOException) {
                    _postLikeError.value = Unit
                }
            }
        }
    }

    fun removePost(id: Long) {
        viewModelScope.launch {
            try {
                repository.removePost(id)
            } catch (e: IOException) {
                _postRemoveError.value = Unit
            }
        }
    }

    fun refreshingPosts() {
        viewModelScope.launch {
            _state.value = FeedModel(refreshing = true)
            try {
                val posts = repository.getAll()
                _state.value = FeedModel(empty = posts.isEmpty())
            } catch (e: IOException) {
                _state.value = FeedModel(refreshing = false)
                _postsRefreshError.value = Unit
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = FeedModel(loading = true)
            try {
                val posts = repository.getAll()
                _state.value = FeedModel(empty = posts.isEmpty())
            } catch (e: IOException) {
                _state.value = FeedModel(errorVisible = true)
            }
        }
    }

    fun retrySendPost(post: Post) {
        edited.value = post
        savePost()
    }

    fun savePost() {
        viewModelScope.launch {
            edited.value?.let {
                _postCreated.value = Unit
                try {
                    when (_photo.value) {
                        noPhoto -> {
                            val localPost = PostEntity.fromDto(it)
                                .copy(state = PostState.Progress)
                            if (it.id == 0L) {
                                localPost.let { entity ->
                                    localId = repository.savePost(entity)
                                    entity.copy(localId = localId, id = localId)
                                }
                            } else {
                                localId = it.id
                            }
                            val networkPost = repository.sendPost(it)
                            repository.savePost(
                                localPost.copy(
                                    state = PostState.Success,
                                    id = networkPost.id,
                                    localId = localId
                                )
                            )
                        }
                        else -> {
                            _photo.value?.file?.let { file ->
                                repository.saveWithAttachment(it, MediaUpload(file))
                            }
                        }
                    }
                } catch (e: IOException) {
                    repository.savePost(
                        PostEntity.fromDto(it)
                            .copy(
                                state = PostState.Error,
                                localId = localId,
                                id = localId
                            )
                    )
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun sharePost(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(
            intent,
            R.string.chooser_share_post.toString()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }
}