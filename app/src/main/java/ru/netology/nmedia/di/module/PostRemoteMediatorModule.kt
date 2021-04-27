package ru.netology.nmedia.di.module

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.repository.PostRemoteMediator

@OptIn(ExperimentalPagingApi::class)
@InstallIn(SingletonComponent::class)
@Module
abstract class PostRemoteMediatorModule {

    @Binds
    abstract fun bindPostRemoteMediator(mediator: PostRemoteMediator): RemoteMediator<Int, PostEntity>
}