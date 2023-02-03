package com.github.mantasjasikenas.namiokai.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.utils.Constants.DOWNLOAD_CHANNEL_ID
import java.util.Random

class NotificationWorker(private val context: Context, private val workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        startForegroundService()

        return Result.success()
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random().nextInt(),
                NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }

}