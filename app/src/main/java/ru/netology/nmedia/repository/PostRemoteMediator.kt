package ru.netology.nmedia.repository

import androidx.paging.*
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.model.AppError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val service: PostApiService,
    private val postDao: PostDao
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> service.getLatest(state.config.initialLoadSize)
                LoadType.PREPEND -> {
                    val item = state.firstItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getAfter(item.id, state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val item = state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBefore(item.id, state.config.pageSize)
                }
            }
            if (!response.isSuccessful) {
                throw AppError(response.code(), response.message())
            }
            val body = response.body() ?: throw AppError(response.code(), response.message())
            postDao.insert(body.toEntity())
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}