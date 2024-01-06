package com.rajit.workmanagerexample

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rajit.workmanagerexample.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

const val DATA_KEY_DESC = "data_key_desc"

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (!isGranted) {
                showContextUI()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        requestNotificationPermission()

        // Sending Data via WorkRequest to MyWorker
        val data = Data.Builder()
            .putString(DATA_KEY_DESC, "Hey, i'm sending work manager data")
            .build()

        // Adding Constraints to the WorkRequest
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // Only executes the work if the device's battery isn't low
            .build()

        // Network related Constraint
        val networkConstraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Creating WorkRequest of type PeriodicWorkRequest which has a minimum specified time of 15 Minutes by the System
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
                .setInputData(data)
                .setConstraints(networkConstraint)
                .build()

        // Second Point of Contact - WorkRequest of type OneTime
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data) // Binding Input Data to OneTimeWorkRequest
            .setConstraints(constraints) // Setting the Constraints
            .build()

        _binding.performWorkBtn.setOnClickListener {
            // Third Point of Contact - Enqueue the WorkRequest through WorkManager
            WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest)
        }

        _binding.performPeriodicWorkBtn.setOnClickListener {

            // While using PeriodicWorkRequest always try using `enqueuePeriodicWork()`
            // to avoid duplicating the already existing work request that does the exact same thing
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "myWork", // Used to uniquely identify each work request
                ExistingPeriodicWorkPolicy.KEEP, // Keeps the unfinished work and doesn't create a new one
                periodicWorkRequest
            )
        }

        // Optional - Check the progress of the work request
        WorkManager
            .getInstance(applicationContext)
            .getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this) { workInfo ->

                if (workInfo.state.isFinished) {
                    // Getting Output Data from WorkInfo
                    val outputDataFromWorker = workInfo.outputData.getString(OUTPUT_KEY_DESC)
                    _binding.workInfoTV.append("\nResult: $outputDataFromWorker")
                }

                val result = workInfo.state.name
                _binding.workInfoTV.append("\n$result")
            }

        // Periodic Work Observer - Non Unique
//        WorkManager
//            .getInstance(applicationContext)
//            .getWorkInfoByIdLiveData(periodicWorkRequest.id)
//            .observe(this) { workInfo ->
//
//                if (workInfo.state.isFinished) {
//                    val outputDataFromPeriodicWork = workInfo.outputData.getString(OUTPUT_KEY_DESC)
//                    _binding.periodicWorkInfoTV.append("\nResult: $outputDataFromPeriodicWork - ${System.currentTimeMillis()}")
//                }
//
//                val workerState = workInfo.state.name
//                _binding.periodicWorkInfoTV.append("\n$workerState")
//
//            }


        // Observer for Unique Periodic Work
        WorkManager
            .getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData("myWork")
            .observe(this) { workInfoList ->

                workInfoList.forEach { workInfo ->
                    if (workInfo.state.isFinished) {
                        val outputDataFromPeriodicWork = workInfo.outputData.getString(OUTPUT_KEY_DESC)
                        _binding.periodicWorkInfoTV.append("\nResult: $outputDataFromPeriodicWork - ${System.currentTimeMillis()}")
                    }

                    val workerState = workInfo.state.name
                    _binding.periodicWorkInfoTV.append("\n$workerState")
                }

            }

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {

            when {

                ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED -> {

                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) -> {

                    showContextUI()

                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showContextUI() {
        MaterialAlertDialogBuilder(this@MainActivity)
            .setCancelable(false)
            .setTitle("Permission Required")
            .setMessage(
                "Notification permission is required to let you know about the background" +
                        " processes that are currently under use by our application."
            )
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }.show()
    }

}