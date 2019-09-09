@file:JvmName("MockLocation")

package io.saeid.automator.location

import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock

private const val defaultIntervalMillis = 2_000L

fun mockLocation(
    latitude: Double,
    longitude: Double,
    accuracy: Float = 5f,
    bearingAccuracyDegrees: Float = 0.1f,
    verticalAccuracyMeters: Float = 0.1f,
    speedAccuracyMetersPerSecond: Float = 0.01f,
    preserve: Boolean = true,
    preserveInterval: Long = defaultIntervalMillis
) {
    mockLocation(
        createMockLocation(
            latitude,
            longitude,
            accuracy,
            bearingAccuracyDegrees,
            verticalAccuracyMeters,
            speedAccuracyMetersPerSecond
        ),
        preserve,
        preserveInterval
    )
}

fun mockLocation(
    location: Location,
    preserve: Boolean = true,
    preserveInterval: Long = defaultIntervalMillis
) {
    MockLocationAutomator.mock(location, preserve, preserveInterval)
}

fun mockLocations(locations: List<DelayedLocation>) {
    MockLocationAutomator.mock(locations)
}

fun mockLocations(vararg locations: DelayedLocation) {
    MockLocationAutomator.mock(listOf(*locations))
}

internal fun createMockLocation(
    latitude: Double,
    longitude: Double,
    accuracy: Float,
    bearingAccuracyDegrees: Float,
    verticalAccuracyMeters: Float,
    speedAccuracyMetersPerSecond: Float
): Location {
    return Location(LocationManager.GPS_PROVIDER).also {
        it.latitude = latitude
        it.longitude = longitude
        it.accuracy = accuracy
        it.time = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.bearingAccuracyDegrees = bearingAccuracyDegrees
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.verticalAccuracyMeters = verticalAccuracyMeters
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            it.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }
}