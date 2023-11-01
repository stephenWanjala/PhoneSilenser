package com.github.stephenwanjala.phonesilenser.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.stephenwanjala.phonesilenser.services.SilentModeService
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // get the geofencing event from the intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                println("${geofencingEvent.errorCode}")
                return
            }
        }

        // get the transition type and the geofence request id
        val transition = geofencingEvent?.geofenceTransition
        val requestId = geofencingEvent?.triggeringGeofences?.get(0)?.requestId


        val serviceIntent = Intent(context, SilentModeService::class.java)
        serviceIntent.putExtra("transition", transition)
        serviceIntent.putExtra("requestId", requestId)

        // start the foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
