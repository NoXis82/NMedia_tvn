package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.auth.AppAuth

const val TAG_APP = "FCMService"

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val channelId = "remote"
    private val content = "content"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            //NMediaApplication.appAuth.authStateFlow.value.id
          println(message.data[content])

        } catch (e: Exception) {
            Log.e(
                TAG_APP,
                getString(
                    R.string.notification_error,
                    message.data[action]
                )
            )
        }
    }


    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }
}