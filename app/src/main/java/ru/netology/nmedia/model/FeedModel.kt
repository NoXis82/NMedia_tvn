package ru.netology.nmedia.model

data class FeedModel(
    val loading: Boolean = false,
    val errorVisible: Boolean = false,
    val error: ApiError? = null,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
    val visibleFab: Boolean = false
)
