package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity

class PostRepositoryImpl(private val dao: PostDao) : IPostRepository {
    override val posts: LiveData<List<Post>>
        get() = dao.getAll().map {
            // Новые посты всегда сверху. Если серверного id нет, сравниваем по локальным.
            // Локальные всегда выше серверных
            it.sortedWith(Comparator { o1, o2 ->
                when {
                    o1.id == 0L && o2.id == 0L -> o1.localId.compareTo(o2.localId)
                    o1.id == 0L -> -1
                    o2.id == 0L -> 1
                    else -> -o1.id.compareTo(o2.id)
                }
            }).map(PostEntity::toDto)
        }

    override suspend fun getAll(): List<Post> {
        val netPosts = PostsApi.retrofitService.getAll()
        dao.insertOrUpdate(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
    }

    override suspend fun unLikeById(id: Long) {
        //   val netPost = PostsApi.retrofitService.unLikeById(id)
        dao.likeById(id)
        //return netPost
    }

    override suspend fun likeById(id: Long) {
        //    val netPost = PostsApi.retrofitService.likeById(id)
        dao.likeById(id)
        //return netPost
    }

    override suspend fun removePost(id: Long) {
        PostsApi.retrofitService.removePost(id)
        dao.removeById(id)
    }

    override suspend fun savePost(post: PostEntity) =
        dao.insert(post)

    override suspend fun sendPost(post: Post) =
        PostsApi.retrofitService.savePost(post)
}