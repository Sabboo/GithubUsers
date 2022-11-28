package com.saber.githubusers.work

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkersManager @Inject constructor(applicationContext: Context) {

    private val workManager = WorkManager.getInstance(applicationContext)

    fun manipulateData(uniqueWorkIdentifier: String) {
        val downloadRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()

        workManager
            .beginUniqueWork(
                uniqueWorkIdentifier,
                ExistingWorkPolicy.KEEP,
                downloadRequest
            )
            .enqueue()
    }
}