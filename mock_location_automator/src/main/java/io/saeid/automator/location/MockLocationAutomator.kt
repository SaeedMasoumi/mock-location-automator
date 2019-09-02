package io.saeid.automator.location

import android.content.Context
import android.location.Location
import io.saeid.automator.location.provider.FusedLocationMockProvider
import io.saeid.automator.location.provider.LocationManagerMockProvider

internal object MockLocationAutomator {

    private val mockProviders = listOf(LocationManagerMockProvider(), FusedLocationMockProvider())
    private var isStarted = false

    @Synchronized
    fun start(context: Context) {
        if (isStarted) throw IllegalStateException("MockLocationAutomator is already started.")
        mockProviders.forEach { it.start(context) }
        isStarted = true
    }

    @Synchronized
    fun mock(location: Location, preserve: Boolean) {

    }

    @Synchronized
    fun stop() {
        mockProviders.forEach { it.stop() }
        isStarted = false
    }
}