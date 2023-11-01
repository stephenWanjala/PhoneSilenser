package com.github.stephenwanjala.phonesilenser.feature_silense_phone

sealed interface SilentEvent {
    data object LocationEnabled : SilentEvent
    data object LocationDisabled : SilentEvent

}