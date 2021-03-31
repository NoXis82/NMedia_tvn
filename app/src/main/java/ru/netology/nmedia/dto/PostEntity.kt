package ru.netology.nmedia.dto

import androidx.room.*
import ru.netology.nmedia.enumeration.PostState

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long,
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
    val state: PostState = PostState.Success,
    val visibleState: Boolean = false
  //  var attachment: Attachment? = null
) {

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
            state
            //   attachment
        )



    companion object {
        fun fromDto(dto: Post) = PostEntity(
            0,
            dto.id,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.likes,
            dto.share,
            dto.chat,
            dto.views,
            dto.likedByMe
          //  dto.attachment
        )
    }
    class PostStateConverter {
        @TypeConverter
        fun toPostState(raw: String) : PostState = PostState.values()
            .find { it.name == raw } ?: PostState.Success

        @TypeConverter
        fun fromPostState(postState: PostState): String = postState.name
    }
}

fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)

