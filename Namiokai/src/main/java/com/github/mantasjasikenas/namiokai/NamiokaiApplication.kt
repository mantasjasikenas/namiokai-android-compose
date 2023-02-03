package com.github.mantasjasikenas.namiokai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class NamiokaiApplication : Application() {
    /*override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            Constants.DOWNLOAD_CHANNEL_ID,
            "File download",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }*/
}