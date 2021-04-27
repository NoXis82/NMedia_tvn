package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewer(posts: List<PostEntity>)

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun max(): Long

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Query(
        """UPDATE PostEntity SET 
                    likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END, 
                  likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                  WHERE id = :id"""
    )
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query(
        """UPDATE PostEntity SET
            share = share + 1 WHERE id = :id"""
    )
    fun share(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM PostEntity WHERE id = :id)")
    fun isRowIsExist(id: Long): Boolean

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