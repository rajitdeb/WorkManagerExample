# WorkManagerExample

A sample app to demonstrate the usage of Work Manager in Android

## Overview

This guide provides a step-by-step walkthrough for implementing a simple background task using
WorkManager in Android. WorkManager is part of the Android Jetpack library, designed to make
background processing in Android more manageable.

## Implementation

In this section, we discuss the implementation of `PeriodicWorkRequest` of `WorkManager`. What this
does is that, it executes the business logic inside it periodically based on the time period
specified.

### 1. Create MyWorker Class

Create a `MyWorker` class by extending the `androidx.Worker` class and passing the `context`
and `workerParams` as parameters.

```kotlin
class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    // Class implementation
}
```

### 2. Override the doWork Method

Override the `doWork` method within the `MyWorker` class to contain the background task logic.

```kotlin
class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Business logic goes here
        // Return Result.success(), Result.failure(), or Result.retry() based on the business logic
        return Result.success()
    }
}
```

#### 2.1 Input Data and Output Data

In this section, we send input data to the `MyWorker` class from `MainActivity` and send output data
from the `MyWorker` class to the MainActivity, where we observe the `periodicWorkRequest`.

```kotlin
// Sending Data via WorkRequest to MyWorker
val data = Data.Builder()
    .putString(DATA_KEY_DESC, "Hey, i'm sending work manager data")
    .build()

// Second Point of Contact - WorkRequest of type OneTime
val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
    .setInputData(data) // Binding Input Data to OneTimeWorkRequest
    .build()
```

```kotlin
// Retrieving input data in MyWorker.kt provided by PeriodicWorkRequest from MainActivity
val inputDescData = inputData.getString(DATA_KEY_DESC)

if (inputDescData != null) {
    displayNotification("Test Task", inputDescData)
}

// Sending output data to the WorkInfo Observer
val data = Data.Builder()
    .putString(OUTPUT_KEY_DESC, "This is Output Work Data :)")
    .build()

// Passing the output data via Result.Success()
return Result.success(data)

```

#### 2.2 Adding Constraints

In this section, we add constraints to the `PeriodicWorkRequest` to only execute the work if the
device has a decent amount of battery left and is not completely drained.

```kotlin
// Network related Constraint
val networkConstraint = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

```

### 3. Create a OneTimeRequest

Create a `PeriodicWorkRequest` (a direct subclass of WorkRequest) using
the `PeriodicWorkRequest.Builder()`

```kotlin
// Creating WorkRequest of type PeriodicWorkRequest which has a minimum specified time of 15 Minutes by the System
val periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
    .setInputData(data)
    .setConstraints(networkConstraint)
    .build()
```

### 4. Enqueue the Work Request

Enqueue the `periodicWorkRequest` using the following command:

```kotlin
WorkManager.getInstance(context).enqueue(periodicWorkRequest)
```

### Optional: Track Work Progress

Optionally, track the progress of the work by observing the work status. Use
the `getWorkInfoByIdLiveData` method to get real-time updates on the work status.

```kotlin
WorkManager.getInstance(applicationContext)
    .getWorkInfoByIdLiveData(periodicWorkRequest.id)
    .observe(this) { workInfo ->
        val result = workInfo.state.name
        // Handle the work status as needed
    }
```

#### Receiving the Output Data in the WorkManager Observer in MainActivity

```kotlin
// Optional - Check the progress of the work request
WorkManager
    .getInstance(applicationContext)
    .getWorkInfoByIdLiveData(periodicWorkRequest.id)
    .observe(this) { workInfo ->

        if (workInfo.state.isFinished) {
            // Getting Output Data from WorkInfo
            val outputDataFromWorker = workInfo.outputData.getString(OUTPUT_KEY_DESC)
            _binding.workInfoTV.append("\nResult: $outputDataFromWorker")
        }

        val result = workInfo.state.name
        _binding.workInfoTV.append("\n$result")
    }
```