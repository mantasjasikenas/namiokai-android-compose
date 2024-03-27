package com.github.mantasjasikenas.namiokai.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.github.mantasjasikenas.core.database.Notification
import com.github.mantasjasikenas.core.domain.repository.NotificationsRepository
import com.github.mantasjasikenas.namiokai.MainActivity
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.workers.NotificationWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FirebaseMessagingService"

@AndroidEntryPoint
class FirebaseCloudMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    override fun onNewToken(token: String) {
        Log.d(
            TAG,
            "Refreshed token: $token"
        )
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(
            TAG,
            "From: ${remoteMessage.from}"
        )
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(
                TAG,
                "Message data payload: ${remoteMessage.data}"
            )
        }
        remoteMessage.notification?.let {
            Log.d(
                TAG,
                "Message Notification Body: ${it.body}"
            )

            coroutineScope.launch {
                notificationsRepository.insertNotification(
                    Notification(
                        title = it.title ?: "",
                        text = it.body ?: "",
                        date = System.currentTimeMillis()
                    )
                )
            }


            sendNotification(
                messageTitle = it.title,
                messageBody = it.body
            )
        }
    }


    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .build()
        WorkManager.getInstance(this)
            .beginWith(work)
            .enqueue()
    }

    private fun handleNow() {
        Log.d(
            TAG,
            "Short lived task is done."
        )
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(
            TAG,
            "sendRegistrationTokenToServer($token)"
        )
    }

    private fun sendNotification(
        messageTitle: String? = "Namiokai",
        messageBody: String? = ""
    ) {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,
            channelId
        )
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Main channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(
            0,
            notificationBuilder.build()
        )
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}
