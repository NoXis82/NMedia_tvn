package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.netology.nmedia.dto.*

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

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

    @Query("SELECT EXISTS(SELECT * FROM PostEntity WHERE id = :id)")
    fun isRowIsExist(id : Long) : Boolean

    // Несколько вызовов лучше объединять в транзакции, чтобы не было гонки
    @Transaction
    suspend fun insertOrUpdate(posts: List<PostEntity>) {
        posts.forEach {
            if (isRowIsExist(it.id)) {
                updateContentById(it.id, it.content)
            } else {
                insert(it)
            }
        }
    }
}