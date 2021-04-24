package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.enumeration.PostState
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.IPostRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import ru.netology.nmedia.work.*
import java.io.IOException
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: IPostRepository,
    private val workManager: WorkManager,
    private val auth: AppAuth
) : ViewModel() {

    var isHandledBackPressed: String = ""
    private val empty = Post(
        id = 0,
        content = "",
        authorId = 0,
        author = "",
        authorAvatar = "",
        published = ""
    )
    private val noPhoto = PhotoModel()
    private var localId = 0L
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

//    val postsDao: Flow<PagingData<Post>> = auth
//        .authStateFlow
//        .flatMapLatest { (myId, _) ->
//            repository.postsDao
//                .map { posts ->
//                    posts.map { post ->
//                        post.copy(ownedByMe = post.authorId == myId)
//                    }
//                }
//        }

    val posts: Flow<PagingData<Post>> = auth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.posts
                .map { posts ->
                    posts.map { post ->
                        post.copy(ownedByMe = post.authorId == myId)
                    }
                }
        }

    private val _postsRefreshError = SingleLiveEvent<Unit>()
    val postsRefreshError: LiveData<Unit>
        get() = _postsRefreshError

    private val _postRemoveError = SingleLiveEvent<Unit>()
    val postRemoveError: LiveData<Unit>
        get() = _postRemoveError

    private val _postLikeError = SingleLiveEvent<Unit>()
    val postLikeError: LiveData<Unit>
        get() = _postLikeError

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun checkSignIn(): Boolean {
        return auth.authStateFlow.value.id != 0L
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
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
                val data = workDataOf(RemovePostWorker.postKey to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)
            } catch (e: IOException) {
                _postRemoveError.value = Unit
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
            edited.value?.let { post ->
                _postCreated.value = Unit
                try {
                    val id = when (_photo.value) {
                        noPhoto -> repository.prepareWork(post)
                        else -> _photo.value?.uri?.let {
                            repository.saveWork(post, MediaUpload(it.toFile()))
                        }
                    }
                    val data = workDataOf(SavePostWorker.postKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)
                } catch (e: IOException) {
                    repository.savePost(
                        PostEntity.fromDto(post)
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
}