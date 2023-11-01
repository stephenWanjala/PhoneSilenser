package com.github.stephenwanjala.phonesilenser.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.stephenwanjala.phonesilenser.feature_silense_phone.SilentEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SilenceViewModel @Inject constructor() : ViewModel() {
    private val locationEnabledState = MutableStateFlow(LocationEnabledState())
    val locationEnabled = locationEnabledState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        LocationEnabledState()
    )


    fun onEvent(event: SilentEvent) {
        when (event) {
            is SilentEvent.LocationEnabled -> {
                locationEnabledState.value = LocationEnabledState(true)
            }

            is SilentEvent.LocationDisabled -> {
                locationEnabledState.value = LocationEnabledState(false)
            }
        }
    }


}


data class LocationEnabledState(val isLocationEnabled: Boolean = false)

