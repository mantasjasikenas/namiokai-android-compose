package com.example.namiokai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.namiokai.utils.Constants
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