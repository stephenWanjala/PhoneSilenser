package com.github.stephenwanjala.phonesilenser.location

sealed interface LocationEvent {
    data object LocationEnabled : LocationEvent
    data object LocationDisabled : LocationEvent

}