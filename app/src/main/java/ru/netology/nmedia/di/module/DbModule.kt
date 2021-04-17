package ru.netology.nmedia.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.db.AppDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDb {
        return  Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .build()
    }
}