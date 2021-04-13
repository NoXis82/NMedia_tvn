package ru.netology.nmedia.dao

import androidx.room.*
import ru.netology.nmedia.entity.PostWorkEntity

@Dao
interface PostWorkDao {

    @Query("SELECT * FROM PostWorkEntity WHERE localId = :id")
    suspend fun getById(id: Long): PostWorkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(work: PostWorkEntity): Long

    @Query("DELETE FROM PostWorkEntity WHERE localId = :id")
    suspend fun removeById(id: Long)

}