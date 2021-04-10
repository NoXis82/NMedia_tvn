package ru.netology.nmedia.db

import android.content.Context
import androidx.room.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostWorkDao
import ru.netology.nmedia.entity.*


@Database(entities = [PostEntity::class, PostWorkEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [PostStateConverter::class, Converters::class])
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postWorkDao(): PostWorkDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .build()
    }
}