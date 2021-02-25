package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.dto.*
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id ASC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.localId == 0L) {
            insert(
                post.copy(
                    author = "Student",
                    authorAvatar = "netology.jpg",
                    published = SimpleDateFormat(
                        "dd MMMM yyyy в HH:mm",
                        Locale.ENGLISH
                    )
                        .format(Date())
                )
            )
        } else {
            updateContentById(post.id, post.content)
        }

    @Query(
        """UPDATE PostEntity SET 
                    likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END, 
                  likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                  WHERE id = :id"""
    )
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE localId = :id")
    suspend fun removeById(id: Long)

    @Query(
        """UPDATE PostEntity SET
            share = share + 1 WHERE id = :id"""
    )
    fun share(id: Long)

    @Query ("SELECT COUNT (*) FROM PostEntity")
    suspend fun count(): Long
}