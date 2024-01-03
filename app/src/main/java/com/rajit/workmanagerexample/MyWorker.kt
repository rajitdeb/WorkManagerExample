package com.rajit.workmanagerexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

// First point of contact - Worker Class
class MyWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        displayNotification("Test Task", "Task completed successfully!")
        return Result.success()
    }

    private fun displayNotification(title: String, description: String) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                "WorkManagerExampleApp",
                "WorkManagerExampleApp",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "WorkManagerExampleApp").apply {
            setContentTitle(title)
            setContentText(description)
            setSmallIcon(R.mipmap.ic_launcher)
        }

        notificationManager.notify(1, notification.build())

    }

}