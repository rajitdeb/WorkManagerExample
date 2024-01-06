Skip to content
rajitdeb
/
WorkManagerExample

Type / to search

Code
Issues
Pull requests
Actions
Projects
Wiki
Security
Insights
Settings
Files
Go to file
t
.idea
app
gradle
.gitignore
PERIODIC_WORK_REQUEST.md
README.md
build.gradle.kts
gradle.properties
gradlew
gradlew.bat
settings.gradle.kts
Editing PERIODIC_WORK_REQUEST.md in WorkManagerExample
BreadcrumbsWorkManagerExample
/
PERIODIC_WORK_REQUEST.md
in
master

Edit

Preview
Indent mode

Spaces
Indent size

4
Line wrap mode

Soft wrap
Editing PERIODIC_WORK_REQUEST.md file contents
Selection deleted
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
110
111
112
113
114
115
116
117
118
119
120
121
122
123
124
125
126
127
128
129
130
131
132
133
134
135
136
137
138
139
140
141
142
143
144
145
146
147
148
149
150
151
152
153
154
155
156
157
158
159
160
161
162
163
164
165
166
167
168
169
170
171
172
173
174
175
176
177
178
179
180
181
182
183
184
185
186
187
the `PeriodicWorkRequest.Builder()`

```kotlin
// Creating WorkRequest of type PeriodicWorkRequest which has a minimum specified time of 15 Minutes by the System
val periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
    .setInputData(data)
    .setConstraints(networkConstraint)
    .build()
```

### 4. Enqueue the Work Request

✔️<b>BEST PRACTICE:</b><br>
Whenever we're using periodic work request we should always use unique periodic work request in order to prevent duplicate work requests fulfilling the same purpose. So the command is:

```kotlin
// While using PeriodicWorkRequest always try using `enqueuePeriodicWork()`
// to avoid duplicating the already existing work request that does the exact same thing
WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
    "myWork", // Used to uniquely identify each work request
    ExistingPeriodicWorkPolicy.KEEP, // Keeps the unfinished work and doesn't create a new one
    periodicWorkRequest
)
```

❌<b>NOT SO-GOOD PRACTICE:</b><br>
Enqueue the `periodicWorkRequest` using the following command:

```kotlin
WorkManager.getInstance(context).enqueue(periodicWorkRequest)
```

### Optional: Track Work Progress

#### Observing Unique Periodic Work Request (by name)
```kotlin
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
```


#### Observing Generic Periodic Work Request
Track the progress of the generic periodic work by observing the work status. Use
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
### Cancelling the Unique Periodic Work
To cancel the unique periodic work, we make use of the `cancelUniqueWork()` method and pass the `name` of the work as parameter. It only finishes the unfinished work and not the ongoing one. The command for cancelling unique periodic work is:
```kotlin
// Stop the Unfinished already queued Unique Periodic Work Request
WorkManager.getInstance(applicationContext).cancelUniqueWork("myWork")
```
Use Control + Shift + m to toggle the tab key moving focus. Alternatively, use esc then tab to move to the next interactive element on the page.
No file chosen
Attach files by dragging & dropping, selecting or pasting them.
Editing WorkManagerExample/PERIODIC_WORK_REQUEST.md at master · rajitdeb/WorkManagerExample