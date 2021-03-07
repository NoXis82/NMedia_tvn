package ru.netology.nmedia.dto

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PostEntity(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val share: Int = 0,
    val chat: Int = 0,
    val views: Int = 0,
    val likedByMe: Boolean = false,
    val addDao: Boolean
  //  var attachment: Attachment? = null
) {
    @PrimaryKey(autoGenerate = true)
    var localId: Long = id

  fun toDto() =
        Post(
            id,
            author,
            authorAvatar,
            content,
            published,
            likes,
            share,
            chat,
            views,
            likedByMe,
            addDao
            //   attachment
        )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            dto.id,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.likes,
            dto.share,
            dto.chat,
            dto.views,
            dto.likedByMe,
            dto.addDao
          //  dto.attachment
        )
    }
}
