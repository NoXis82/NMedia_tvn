package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.auth.AuthState

class AuthViewModel : ViewModel() {
    val data: LiveData<AuthState> = NMediaApplication.appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = NMediaApplication.appAuth.authStateFlow.value.id != 0L
}