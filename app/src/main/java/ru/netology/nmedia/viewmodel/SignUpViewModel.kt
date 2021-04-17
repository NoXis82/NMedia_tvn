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
class SignUpViewModel @Inject constructor(
    private val auth: AppAuth,
    private val repository: IPostRepository
) : ViewModel() {

    fun regUserData(login: String, pass: String, name: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = repository.regUser(login, pass, name)
                auth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}