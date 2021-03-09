package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    val published: String = "",
    val likes: Int = 0,
    val share: Int = 0,
    val chat: Int = 0,
    val views: Int = 0,
    val likedByMe: Boolean = false,
    val state: PostState = PostState.Success
    //   var attachment: Attachment? = null
)

enum class PostState {
    Progress,
    Success,
    Error
}
