package ru.netology.nmedia.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.*
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.*
import ru.netology.nmedia.enumeration.*
import ru.netology.nmedia.model.ApiError
import ru.netology.nmedia.model.AppError
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postWorkDao: PostWorkDao,
    private val apiService: PostApiService,
    private val auth: AppAuth,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    remoteMediator: RemoteMediator<Int, PostEntity>
) : IPostRepository {

    companion object {
        const val PAGE_SIZE = 5
    }

    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = remoteMediator,
        pagingSourceFactory = postDao::pagingSource
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun refreshPostsWorker() {
        val id = postRemoteKeyDao.max() ?: postDao.max()
        val response = apiService.getAfter(id, PAGE_SIZE)
        if (!response.isSuccessful) {
            throw AppError(response.code(), response.message())
        }
        val body = response.body() ?: throw AppError(response.code(), response.message())
        postDao.insertOrUpdate(body.toEntity())
        postRemoteKeyDao.insert(
            listOf(
                PostRemoteKeyEntity(
                    type = PostRemoteKeyEntity.KeyType.AFTER,
                    id = body.first().id
                ),
                PostRemoteKeyEntity(
                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                    id = body.last().id
                )
            )
        )
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            val newer = apiService.getNewer(id)
            emit(newer.size)
        }
    }
        .catch { e -> throw ApiError.fromThrowable(e) }
        .flowOn(Dispatchers.Default)

    override fun getNewerList(id: Long): Flow<List<Post>> = flow {
        val posts = apiService.getNewer(id)
        emit(posts)
    }
        .catch { e -> throw ApiError.fromThrowable(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        val netPosts = apiService.getAll()
        postDao.insertOrUpdate(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
    }

    override suspend fun unLikeById(id: Long) {
        apiService.unLikeById(id)
        postDao.likeById(id)
    }

    override suspend fun likeById(id: Long) {
        apiService.likeById(id)
        postDao.likeById(id)
    }

    override suspend fun removePost(id: Long) {
        apiService.removePost(id)
        postDao.removeById(id)
    }

    override suspend fun sendNewer(posts: List<Post>) =
        postDao.insertOrUpdate(posts.map(PostEntity.Companion::fromDto))

    override suspend fun upload(upload: MediaUpload): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            upload.file.name,
            upload.file.asRequestBody()
        )
        return apiService.upload(media)
    }

    override suspend fun updateUser(login: String, pass: String): AuthState {
        return apiService.updateUser(login, pass)
    }

    override suspend fun regUser(login: String, pass: String, name: String): AuthState {
        return apiService.regUser(login, pass, name)
    }

    override suspend fun saveWork(post: Post, upload: MediaUpload): Long {
        val entity = PostWorkEntity.fromDto(post).apply {
            this.uri = upload.file.toURI().toString()
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
                                authorId = auth.authStateFlow.value.id,
                                state = PostState.Progress
                            )
                    )
                }
                else -> {
                    val media = upload(MediaUpload(Uri.parse(workEntity.uri).toFile()))
                    PostEntity.fromWorkDto(
                        workEntity
                            .copy(
                                authorId = auth.authStateFlow.value.id,
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
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun savePost(post: PostEntity) = postDao.insert(post)

    override suspend fun sendPost(post: Post): Post = apiService.savePost(post)

}
