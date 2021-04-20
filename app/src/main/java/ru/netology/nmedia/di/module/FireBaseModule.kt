package ru.netology.nmedia.di.module

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FireBaseModule {
    @Provides
    @Singleton
    fun providesFirebaseInstallations(): FirebaseInstallations =
        FirebaseInstallations.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()

}