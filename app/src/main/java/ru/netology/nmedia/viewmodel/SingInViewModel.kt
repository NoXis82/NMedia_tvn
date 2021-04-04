package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.application.NMediaApplication.Companion.repository
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext

class SingInViewModel : ViewModel() {

    fun getUserLogin(login: String, pass: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = repository.updateUser(login, pass)
                NMediaApplication.appAuth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}