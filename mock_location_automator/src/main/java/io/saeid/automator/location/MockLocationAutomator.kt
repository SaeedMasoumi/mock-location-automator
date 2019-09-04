package io.saeid.automator.location

import android.content.Context
import android.location.Location
import io.saeid.automator.location.provider.FusedLocationMockProvider
import io.saeid.automator.location.provider.LocationManagerMockProvider

object MockLocationAutomator {

    private const val waitingTime = 200L // millis

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
        if (!isStarted) throw IllegalStateException("start() method must be called before mocking any location.")
        mockProviders.forEach { it.mock(location) }
        Thread.sleep(waitingTime)
    }

    @Synchronized
    fun stop() {
        mockProviders.forEach { it.stop() }
        isStarted = false
    }
}