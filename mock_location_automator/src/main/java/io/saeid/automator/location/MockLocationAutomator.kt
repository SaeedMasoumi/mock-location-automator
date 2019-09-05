package io.saeid.automator.location

import android.content.Context
import android.location.Location
import androidx.annotation.VisibleForTesting
import io.saeid.automator.location.provider.FusedLocationMockProvider
import io.saeid.automator.location.provider.LocationManagerMockProvider
import io.saeid.automator.location.provider.MockProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlin.coroutines.CoroutineContext

object MockLocationAutomator : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private const val waitingTime = 200L // millis

    private var mockProviders = listOf(LocationManagerMockProvider(), FusedLocationMockProvider())
    private var isStarted = false
    private var updateChannel: ReceiveChannel<Unit>? = null
    private var updateJob: Job? = null

    @VisibleForTesting
    internal fun setProviders(vararg providers: MockProvider) {
        mockProviders = listOf(*providers)
    }

    @Synchronized
    fun start(context: Context) {
        if (isStarted) throw IllegalStateException("MockLocationAutomator is already started.")
        mockProviders.forEach { it.start(context) }
        isStarted = true
    }

    @Synchronized
    fun mock(location: Location, preserve: Boolean, preserveInterval: Long) {
        if (!isStarted) throw IllegalStateException("start() method must be called before mocking any location.")
        if (preserve) {
            registerFixedUpdates(location, preserveInterval)
        } else {
            dispatchToProviders(location)
        }
        Thread.sleep(waitingTime)
    }

    @Synchronized
    fun mock(locations: List<DelayedLocation>) {
        if (!isStarted) throw IllegalStateException("start() method must be called before mocking any location.")
        registerBatchUpdates(locations)
        Thread.sleep(waitingTime)
    }

    @Synchronized
    fun stop() {
        cancelLocationUpdates()
        mockProviders.forEach { it.stop() }
        isStarted = false
    }

    private fun dispatchToProviders(location: Location) {
        mockProviders.forEach { it.mock(location) }
    }

    private fun registerFixedUpdates(location: Location, interval: Long) {
        cancelLocationUpdates()
        updateChannel = ticker(delayMillis = interval, initialDelayMillis = 0)
        updateJob = launch {
            for (event in updateChannel!!) {
                dispatchToProviders(location)
            }
        }
    }

    private fun registerBatchUpdates(locations: List<DelayedLocation>) {
        cancelLocationUpdates()
        updateJob = launch {
            locations.forEach {
                delay(it.delay)
                dispatchToProviders(it.location)
            }
        }
    }

    private fun cancelLocationUpdates() {
        updateChannel?.cancel()
        updateJob?.cancel()
    }
}