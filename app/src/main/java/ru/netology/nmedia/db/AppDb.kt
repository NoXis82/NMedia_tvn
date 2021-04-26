package ru.netology.nmedia.db

import androidx.room.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.dao.PostWorkDao
import ru.netology.nmedia.entity.*

@Database(
    entities = [PostEntity::class, PostWorkEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [PostStateConverter::class, Converters::class, PostRemoteKeyConverter::class])
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}