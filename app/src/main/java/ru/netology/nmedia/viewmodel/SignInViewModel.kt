package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.IPostRepository
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val auth: AppAuth,
    private val repository: IPostRepository
) : ViewModel() {

    fun getUserLogin(login: String, pass: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = repository.updateUser(login, pass)
                auth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}