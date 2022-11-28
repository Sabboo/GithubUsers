package com.saber.githubusers.work

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.annotation.StringRes
import androidx.work.*
import com.saber.githubusers.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.R.drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat.Builder
import com.saber.githubusers.data.User
import com.saber.githubusers.di.DatabaseModule
import com.saber.githubusers.di.NetworkModule
import com.saber.githubusers.utils.checkCachedAvatarExists
import okhttp3.ResponseBody


class ImageDownloadWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private val api =
        NetworkModule.providesAPI(NetworkModule.provideRetrofit(NetworkModule.provideHttpClient()))

    private val db = DatabaseModule.provideDatabase(context.applicationContext)

    override suspend fun doWork(): Result {
        try {
            db.users().getCachedUsers()
                .filterNot { user -> user.checkCachedAvatarExists(context) }
                .forEach { user ->
                    val response = api.downloadImageWithDynamicUrl(user.avatar)!!
                    if (response.isSuccessful && response.body() != null)
                        handleSuccessResponse(response.body()!!, user)
                    else
                        return Result.failure(
                            workDataOf("ERROR_MSG" to "Failure while downloading image")
                        )
                }
            return Result.success()
        } catch (ioException: IOException) {
            return Result.retry()
        } catch (throwable: Throwable) {
            return Result.failure()
        }
    }

    private suspend fun handleSuccessResponse(body: ResponseBody, user: User): Result {
        return withContext(Dispatchers.IO) {
            // Create app specific file in internal storage
            val file = File(
                context.cacheDir,
                "${user.id}-${user.name}.png"
            )
            // Write response stream to the created file
            val outputStream = FileOutputStream(file)
            outputStream.use { stream ->
                try {
                    stream.write(body.bytes())
                } catch (e: IOException) {
                    return@withContext Result.failure()
                }
            }
            Result.success()
        }
    }

    /**
     * Create ForegroundInfo required to run a Worker in a foreground service.
     */
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notificationId = 1
        return ForegroundInfo(notificationId, createNotification())
    }

    /**
     * Create the notification and required channel (O+) for running work in a foreground service.
     */
    private fun createNotification(): Notification {
        val channelId = getString(R.string.notification_channel_id)
        val title = getString(R.string.notification_title)
        val cancel = getString(R.string.cancel_download)
        val name = getString(R.string.channel_name)
        // This PendingIntent can be used to cancel the Worker.
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val builder = Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setTicker(title)
            .setOngoing(true)
            .addAction(drawable.ic_delete, cancel, intent)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            createNotificationChannel(channelId, name).also {
                builder.setChannelId(it.id)
            }
        }
        return builder.build()
    }


    private fun getString(@StringRes id: Int) = applicationContext.getString(id)

    /**
     * Create the required notification channel for O+ devices.
     */
    @TargetApi(VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        name: String
    ): NotificationChannel {
        return NotificationChannel(
            channelId, name, NotificationManager.IMPORTANCE_LOW
        ).also { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }
}
