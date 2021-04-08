package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.PushToken
import kotlin.random.Random

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
            val recipientId = gson.fromJson(message.data[content], Content::class.java).recipientId
            val content = gson.fromJson(message.data[content], Content::class.java).content
            val authStateId = NMediaApplication.appAuth.authStateFlow.value.id
            if (recipientId == authStateId) {
                notification(content)
            }
            if (recipientId == 0L || recipientId != 0L && recipientId != authStateId) {
                AppAuth.getInstance().sendPushToken(token = AppAuth.getInstance().tokenSave)
            }
            if (recipientId == null) {
                notification(content)
            }
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

    private fun notification(content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.posts_avatars_drawable)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

}

data class Content(
    val recipientId: Long? = null,
    val content: String
)