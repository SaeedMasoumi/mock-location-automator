package io.saeid.automator.location

import android.location.Location

class DelayedLocation(val location: Location, val delay: Long) {

    constructor(
        latitude: Double,
        longitude: Double,
        accuracy: Float = 5f,
        bearingAccuracyDegrees: Float = 0.1f,
        verticalAccuracyMeters: Float = 0.1f,
        speedAccuracyMetersPerSecond: Float = 0.01f,
        delay: Long
    ) : this(
        createMockLocation(
            latitude,
            longitude,
            accuracy,
            bearingAccuracyDegrees,
            verticalAccuracyMeters,
            speedAccuracyMetersPerSecond
        ), delay
    )
}