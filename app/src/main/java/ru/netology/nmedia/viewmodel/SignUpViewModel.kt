package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.application.NMediaApplication
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext

class SignUpViewModel : ViewModel() {

    fun regUserData(login: String, pass: String, name: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = NMediaApplication.repository.regUser(login, pass, name)
                NMediaApplication.appAuth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}