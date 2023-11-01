package com.github.stephenwanjala.phonesilenser.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.stephenwanjala.phonesilenser.MainActivity
import com.github.stephenwanjala.phonesilenser.R
import com.google.android.gms.location.Geofence


// a foreground service class to show a notification and adjust the silent mode
class SilentModeService : Service() {

    private val channelId = "SilentModeChannel"
    private val notificationId = 1

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // get the transition type and the geofence request id from the intent
        val transition = intent?.getIntExtra("transition", -1) ?: -1
        val requestId = intent?.getStringExtra("requestId") ?: ""

        // create a notification manager and a notification channel
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Silent Mode Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // create a notification builder and set its properties
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Silent Mode App")
            .setContentText("Your device is in silent mode at $requestId.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // create a pending intent for the main activity and set it as the notification content intent
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val mainActivityPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(mainActivityPendingIntent)

        // start the service in the foreground with the notification
        startForeground(notificationId, notificationBuilder.build())

        // get the audio manager and adjust the silent mode based on the transition and the request id
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                when (requestId) {
                    "work" -> {
                        // if the user enters the work geofence, set the device to silent mode
                        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    }

                    "home" -> {
                        // if the user enters the home geofence, set the device to normal mode
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    }
                }
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                when (requestId) {
                    "work", "home" -> {
                        // if the user exits either the work or the home geofence, set the device to vibrate mode
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    }
                }
            }
        }

        // stop the service and remove it from the foreground
        stopForeground(STOP_FOREGROUND_REMOVE)

        stopSelf()

        return START_NOT_STICKY
    }
}