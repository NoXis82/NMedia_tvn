package ru.netology.nmedia.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostWorkDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.AttachmentEmbeddable
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostWorkEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.PostState
import java.lang.Exception

class PostRepositoryImpl(
    private val dao: PostDao,
    private val postWorkDao: PostWorkDao
) : IPostRepository {
    override val posts: Flow<List<Post>>
        get() = dao.getAll().map {
            it.sortedWith(Comparator { o1, o2 ->
                when {
                    o1.id == 0L && o2.id == 0L -> o1.localId.compareTo(o2.localId)
                    o1.id == 0L -> -1
                    o2.id == 0L -> 1
                    else -> -o1.id.compareTo(o2.id)
                }
            })
                .map(PostEntity::toDto)
        }
            .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            val newer = PostsApi.retrofitService.getNewer(id)
            emit(newer.size)
        }
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    override fun getNewerList(id: Long): Flow<List<Post>> = flow {
        val posts = PostsApi.retrofitService.getNewer(id)
        emit(posts)
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)


    override suspend fun getAll(): List<Post> {
        val netPosts = PostsApi.retrofitService.getAll()
        dao.insertOrUpdate(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
    }

    override suspend fun unLikeById(id: Long) {
        PostsApi.retrofitService.unLikeById(id)
        dao.likeById(id)
    }

    override suspend fun likeById(id: Long) {
        PostsApi.retrofitService.likeById(id)
        dao.likeById(id)
    }

    override suspend fun removePost(id: Long) {
        PostsApi.retrofitService.removePost(id)
        dao.removeById(id)
    }

    override suspend fun sendNewer(posts: List<Post>) =
        dao.insertOrUpdate(posts.map(PostEntity.Companion::fromDto))

    override suspend fun upload(upload: MediaUpload): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            upload.file.name,
            upload.file.asRequestBody()
        )
        return PostsApi.retrofitService.upload(media)
    }

    override suspend fun updateUser(login: String, pass: String): AuthState {
        return PostsApi.retrofitService.updateUser(login, pass)
    }

    override suspend fun regUser(login: String, pass: String, name: String): AuthState {
        return PostsApi.retrofitService.regUser(login, pass, name)
    }

    override suspend fun saveWork(post: Post, upload: MediaUpload): Long {
        val entity = PostWorkEntity.fromDto(post).apply {
            if (upload != null) {
                this.uri = upload.file.toURI().toString()
            }
        }
        return postWorkDao.insert(entity)
    }

    override suspend fun prepareWork(post: Post): Long =
        postWorkDao.insert(PostWorkEntity.fromDto(post))

    override suspend fun processWork(id: Long) {
        var localId: Long
        try {
            val workEntity = postWorkDao.getById(id)
            val postEntity = when (workEntity.uri) {
                null -> {
                    PostEntity.fromWorkDto(
                        workEntity
                            .copy(
                                authorId = NMediaApplication.appAuth.authStateFlow.value.id,
                                state = PostState.Progress
                            )
                    )
                }
                else -> {
                    val media = upload(MediaUpload(Uri.parse(workEntity.uri).toFile()))
                    PostEntity.fromWorkDto(
                        workEntity
                            .copy(
                                authorId = NMediaApplication.appAuth.authStateFlow.value.id,
                                state = PostState.Progress,
                                attachment = AttachmentEmbeddable(media.id, AttachmentType.IMAGE)
                            )
                    )
                }
            }
            if (postEntity.id == 0L) {
                postEntity.let { entity ->
                    localId = savePost(entity)
                    entity.copy(localId = localId, id = localId)
                }
            } else {
                localId = postEntity.id
            }
            val networkPost = sendPost(postEntity.toDto())
            savePost(
                postEntity.copy(
                    id = networkPost.id,
                    localId = localId,
                    state = PostState.Success
                )
            )
            postWorkDao.removeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun savePost(post: PostEntity) = dao.insert(post)

    override suspend fun sendPost(post: Post): Post = PostsApi.retrofitService.savePost(post)

}