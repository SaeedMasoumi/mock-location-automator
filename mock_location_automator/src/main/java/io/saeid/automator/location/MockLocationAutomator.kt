package io.saeid.automator.location

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.SystemClock
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
    private var singleUpdateChannel: ReceiveChannel<Unit>? = null
    private var singleUpdateJob: Job? = null
    private var sequentialUpdateJob: Job? = null

    @VisibleForTesting
    internal fun addProvider(vararg providers: MockProvider) {
        mockProviders = listOf(*providers) + mockProviders
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
        cancelAllJobs()
        if (preserve) {
            registerSingleUpdate(location, preserveInterval)
        } else {
            dispatchToProviders(location)
        }
        Thread.sleep(waitingTime)
    }

    @Synchronized
    fun mock(locations: List<DelayedLocation>) {
        if (!isStarted) throw IllegalStateException("start() method must be called before mocking any location.")
        cancelAllJobs()
        registerSequentialUpdates(locations)
        Thread.sleep(waitingTime)
    }

    @Synchronized
    fun stop() {
        cancelAllJobs()
        mockProviders.forEach { it.stop() }
        isStarted = false
    }

    private fun dispatchToProviders(location: Location) {
        // make sure that we mock a location for current time,
        // otherwise `fusedClient.requestLocationUpdates()` will not work
        val updatedLocation = Location(location).apply {
            time = System.currentTimeMillis()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        }
        mockProviders.forEach { it.mock(updatedLocation) }
    }

    private fun registerSingleUpdate(location: Location, interval: Long) {
        singleUpdateChannel = ticker(delayMillis = interval, initialDelayMillis = 0)
        singleUpdateJob = launch {
            for (event in singleUpdateChannel!!) {
                dispatchToProviders(location)
            }
        }
    }

    private fun registerSequentialUpdates(locations: List<DelayedLocation>) {
        sequentialUpdateJob = launch {
            locations.forEach {
                delay(it.delay)
                cancelSingleUpdateJob()
                registerSingleUpdate(it.location, 1_000L)
            }
        }
    }

    private fun cancelSingleUpdateJob() {
        singleUpdateChannel?.cancel()
        singleUpdateJob?.cancel()
    }

    private fun cancelSequentialUpdateJob() {
        sequentialUpdateJob?.cancel()
    }

    private fun cancelAllJobs() {
        cancelSingleUpdateJob()
        cancelSequentialUpdateJob()
    }
}