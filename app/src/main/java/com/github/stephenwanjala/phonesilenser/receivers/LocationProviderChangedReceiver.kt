package com.github.stephenwanjala.phonesilenser.receivers


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.github.stephenwanjala.phonesilenser.location.LocationEvent
import com.github.stephenwanjala.phonesilenser.location.SilenceViewModel


class LocationProviderChangedReceiver(
    private val viewModel: SilenceViewModel
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isLocationEnabled) viewModel.onEvent(LocationEvent.LocationEnabled)
        if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION ||
            intent?.action == LocationManager.MODE_CHANGED_ACTION
        ) {
            // Location provider && or  mode status has changed


            if (isLocationEnabled) {
                // Location services are enabled, send LocationEnabled event to ViewModel
                viewModel.onEvent(LocationEvent.LocationEnabled)
            } else {
                // Location services are disabled, send LocationDisabled event to ViewModel
                viewModel.onEvent(LocationEvent.LocationDisabled)
//                openLocationSettings(context)
            }
        }
    }
}