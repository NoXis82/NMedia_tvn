package ru.netology.nmedia.adapter

import ru.netology.nmedia.dto.*

interface IOnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
}