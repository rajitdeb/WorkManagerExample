package com.rajit.workmanagerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rajit.workmanagerexample.databinding.ActivityMainBinding

const val DATA_KEY_DESC = "data_key_desc"

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // Sending Data via WorkRequest to MyWorker
        val data = Data.Builder()
            .putString(DATA_KEY_DESC, "Hey, i'm sending work manager data")
            .build()

        // Adding Constraints to the WorkRequest
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // Only executes the work if the device's battery isn't low
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

        // Optional - Check the progress of the work request
        WorkManager
            .getInstance(applicationContext)
            .getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this) { workInfo ->

                if(workInfo.state.isFinished) {
                    // Getting Output Data from WorkInfo
                    val outputDataFromWorker = workInfo.outputData.getString(OUTPUT_KEY_DESC)
                    _binding.workInfoTV.append("\nResult: $outputDataFromWorker")
                }

                val result = workInfo.state.name
                _binding.workInfoTV.append("\n$result")
            }

    }
}