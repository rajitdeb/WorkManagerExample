package com.rajit.workmanagerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rajit.workmanagerexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // Second Point of Contact - WorkRequest of type OneTime
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()

        _binding.performWorkBtn.setOnClickListener {
            // Third Point of Contact - Enqueue the WorkRequest through WorkManager
            WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest)
        }

        // Optional - Check the progress of the work request
        WorkManager
            .getInstance(applicationContext)
            .getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this) { workInfo ->
                val result = workInfo.state.name
                _binding.workInfoTV.append("\n$result")
            }

    }
}