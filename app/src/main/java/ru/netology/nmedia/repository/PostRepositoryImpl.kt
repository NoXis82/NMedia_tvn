package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity

class PostRepositoryImpl(private val dao: PostDao) : IPostRepository {
    override val posts: LiveData<List<Post>>
        get() = dao.getAll().map { it.map(PostEntity::toDto) }

    override suspend fun getAll(): List<Post> {
        val netPosts = PostsApi.retrofitService.getAll()
        dao.insert(netPosts.map(PostEntity.Companion::fromDto))
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

    override suspend fun savePost(post: Post): Post {
        dao.save(PostEntity.fromDto(post))

        return post//PostsApi.retrofitService.savePost(post)
    }

}