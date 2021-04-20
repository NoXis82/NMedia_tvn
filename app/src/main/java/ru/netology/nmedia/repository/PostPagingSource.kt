package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.AppError

class PostPagingSource (
    private val service: PostApiService
) : PagingSource<Long, Post>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> service.getLatest(params.loadSize)
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )
                is LoadParams.Append -> service.getBefore(params.key, params.loadSize)
            }
            if (!response.isSuccessful) {
                throw AppError(response.code(), response.message())
            }
            val body = response.body() ?: throw AppError(response.code(), response.message())
            val nextKey = if (body.isEmpty()) null else body.last().id
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        return null
    }
}