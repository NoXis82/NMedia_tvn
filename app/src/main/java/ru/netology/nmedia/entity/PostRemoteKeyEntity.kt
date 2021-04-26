package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity
class PostRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Long
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}

class PostRemoteKeyConverter {
    @TypeConverter
    fun toPostRemoteKeyType(value: String) = enumValueOf<PostRemoteKeyEntity.KeyType>(value)
    @TypeConverter
    fun fromPostRemoteKeyType(value: PostRemoteKeyEntity.KeyType) = value.name
}