package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.*

interface PostRepository {

    fun get(): LiveData<Post>
    fun like()
    fun share()
    

}