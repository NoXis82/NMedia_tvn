package ru.netology.nmedia.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import java.lang.Exception
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val postWorkDao: PostWorkDao,
    private val apiService: PostApiService,
    private val auth: AppAuth
) : IPostRepository {
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { PostPagingSource(apiService) }
    ).flow


//    override val posts: Flow<List<Post>>
//        get() = dao.getAll().map {
//            it.sortedWith(Comparator { o1, o2 ->
//                when {
//                    o1.id == 0L && o2.id == 0L -> o1.localId.compareTo(o2.localId)
//                    o1.id == 0L -> -1
//                    o2.id == 0L -> 1
//                    else -> -o1.id.compareTo(o2.id)
//                }
//            })
//                .map(PostEntity::toDto)
//        }
//            .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            val newer = apiService.getNewer(id)
            emit(newer.size)
        }
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)

    override fun getNewerList(id: Long): Flow<List<Post>> = flow {
        val posts = apiService.getNewer(id)
        emit(posts)
    }
        .catch { e -> e.printStackTrace() }
        .flowOn(Dispatchers.Default)


    override suspend fun getAll(): List<Post> {
        val netPosts = apiService.getAll()
        dao.insertOrUpdate(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
    }

    override suspend fun unLikeById(id: Long) {
        apiService.unLikeById(id)
        dao.likeById(id)
    }

    override suspend fun likeById(id: Long) {
        apiService.likeById(id)
        dao.likeById(id)
    }

    override suspend fun removePost(id: Long) {
        apiService.removePost(id)
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
            e.printStackTrace()
        }
    }

    override suspend fun savePost(post: PostEntity) = dao.insert(post)

    override suspend fun sendPost(post: Post): Post = apiService.savePost(post)

}