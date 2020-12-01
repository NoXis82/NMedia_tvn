package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity
import ru.netology.nmedia.dto.toPost
import java.text.SimpleDateFormat
import java.util.*

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : IPostRepository {

    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list ->
        list.map {
            it.toPost()
        }
    }

    override fun like(id: Long) {
        dao.likeById(id)
    }

    override fun share(id: Long) {
        dao.share(id)
    }

    override fun removePost(id: Long) {
        dao.removeById(id)
    }

    override fun savePost(post: Post) {
        dao.save(PostEntity.fromPost(post))
    }

}