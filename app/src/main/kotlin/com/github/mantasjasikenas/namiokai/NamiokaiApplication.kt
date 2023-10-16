package com.github.mantasjasikenas.namiokai

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class NamiokaiApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()
    }

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