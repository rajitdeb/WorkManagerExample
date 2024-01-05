package com.rajit.workmanagerexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

const val OUTPUT_KEY_DESC = "output_key_desc"

// First point of contact - Worker Class
class MyWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {

        Log.i("MyWorker", "MyWorker Called!")

        // Retrieving input data provided by OneTimeWorkRequest from MainActivity
        val inputDescData = inputData.getString(DATA_KEY_DESC)

        if(inputDescData != null) {
            displayNotification("Test Task", inputDescData)
        } else {
            displayNotification("Test Task", "This is sample work data!")
        }

        // Sending output data to the WorkInfo Observer
        val data = Data.Builder()
            .putString(OUTPUT_KEY_DESC, "This is Output Work Data :)")
            .build()

        // Passing the output data via Result.Success()
        return Result.success(data)
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