package ru.netology.nmedia.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.application.NMediaApplication.Companion.repository
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext

class SignInViewModel : ViewModel() {

    fun getUserLogin(login: String, pass: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = repository.updateUser(login, pass)
                Log.e("MY", authState.id.toString())
                NMediaApplication.appAuth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}