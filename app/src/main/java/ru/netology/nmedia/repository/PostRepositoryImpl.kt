package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.AttachmentType

class PostRepositoryImpl(private val dao: PostDao) : IPostRepository {
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

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            savePost(PostEntity.fromDto(postWithAttachment))
            sendPost(postWithAttachment)
    }

    override suspend fun upload(upload: MediaUpload): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            upload.file.name,
            upload.file.asRequestBody()
        )
        return PostsApi.retrofitService.upload(media)
    }

    override suspend fun savePost(post: PostEntity) = dao.insert(post)

    override suspend fun sendPost(post: Post): Post = PostsApi.retrofitService.savePost(post)

}