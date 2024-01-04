# WorkManagerExample

A sample app to demonstrate the usage of Work Manager in Android

## Overview

This guide provides a step-by-step walkthrough for implementing a simple background task using
WorkManager in Android. WorkManager is part of the Android Jetpack library, designed to make
background processing in Android more manageable.

## Implementation

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
from the `MyWorker` class to the MainActivity, where we observe the `oneTimeWorkRequest`.

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
// Retrieving input data in MyWorker.kt provided by OneTimeWorkRequest from MainActivity
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

In this section, we add constraints to the `OneTimeWorkRequest` to only execute the work if the
device has a decent amount of battery left and is not completely drained.

```kotlin
val constraints = Constraints.Builder()
    .set.setRequiresBatteryNotLow(true) // Only executes the work if the device's battery isn't low
    .build()

val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
    .setInputData(data) // Binding Input Data to OneTimeWorkRequest
    .setConstraints(constraints) // Setting the Constraints
    .build()
```

### 3. Create a OneTimeRequest

Create a `OneTimeWorkRequest` (a direct subclass of WorkRequest) using
the `OneTimeWorkRequest.Builder()`

```kotlin
val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
```

### 4. Enqueue the Work Request

Enqueue the `oneTimeWorkRequest` using the following command:

```kotlin
WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
```

### Optional: Track Work Progress

Optionally, track the progress of the work by observing the work status. Use
the `getWorkInfoByIdLiveData` method to get real-time updates on the work status.

```kotlin
WorkManager.getInstance(applicationContext)
    .getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
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
```

