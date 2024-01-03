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

### 3. Create a OneTimeRequest

Create a `OneTimeRequest` (a direct subclass of WorkRequest) using the `OneTimeRequest.Builder()`

```kotlin
val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
```

### 4. Enqueue the Work Request

Enqueue the work request using the following command:

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


