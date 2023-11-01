@file:OptIn(ExperimentalPermissionsApi::class)

package com.github.stephenwanjala.phonesilenser.location.presentation



import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.stephenwanjala.phonesilenser.location.SilenceViewModel
import com.github.stephenwanjala.phonesilenser.receivers.LocationProviderChangedReceiver
import com.github.stephenwanjala.phonesilenser.feature_silense_phone.SilentEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun LocationPermissionWrapper(
    viewModel: SilenceViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val locationEnabledState = viewModel.locationEnabled.collectAsState()

    val receiver = LocationProviderChangedReceiver(viewModel)
    DisposableEffect(Unit) {

        val locationFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        val modeFilter = IntentFilter(LocationManager.MODE_CHANGED_ACTION)
        context.registerReceiver(receiver, locationFilter)
        context.registerReceiver(receiver, modeFilter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    LaunchedEffect(isLocationEnabled) {
        viewModel.onEvent(if (isLocationEnabled) SilentEvent.LocationEnabled else SilentEvent.LocationDisabled)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LocationPermissionScreen(
            locationPermissionsState = locationPermissionsState,
            onGrantPermissionClick = {
                locationPermissionsState.launchMultiplePermissionRequest()
            }) {
            if (locationEnabledState.value.isLocationEnabled) content() else Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { openLocationSettings(context) }) {
                    Text(text = "Enable Location Services in Settings")
                }
            }
        }
    }
}

fun openLocationSettings(context: Context) {
    val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(locationSettingsIntent)
}


@Composable
internal fun LocationPermissionScreen(
    locationPermissionsState: MultiplePermissionsState,
    onGrantPermissionClick: () -> Unit,
    onPermissionsGranted: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (locationPermissionsState.allPermissionsGranted) {
            onPermissionsGranted()
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val allPermissionsRevoked =
                        locationPermissionsState.permissions.size ==
                                locationPermissionsState.revokedPermissions.size

                    val textToShow = if (!allPermissionsRevoked) {
                        "Yay! Thanks for letting me access your approximate location. " +
                                "But you know what would be great? If you allow me to know where you " +
                                "exactly are. Thank you!"
                    } else if (locationPermissionsState.shouldShowRationale) {
                        "Getting your exact location is important for this app. " +
                                "Please grant us fine location. Thank you"
                    } else {
                        "To Geofence Needs Location Permission. Allow in Settings"
                    }

                    val buttonText = if (!allPermissionsRevoked) {
                        "Allow precise location"
                    } else {
                        "Request permissions"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = textToShow,
                            textAlign = TextAlign.Center

                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onGrantPermissionClick) {
                        Text(buttonText)
                    }
                }
            }
        }
    }
}



