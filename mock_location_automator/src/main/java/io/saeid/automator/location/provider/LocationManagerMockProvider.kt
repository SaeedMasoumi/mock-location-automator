package io.saeid.automator.location.provider

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build

internal class LocationManagerMockProvider : MockProvider {

    private lateinit var locationManager: LocationManager
    private val supportedProviders =
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
    private val defaultProviderAccuracy = 5 // meter

    override fun start(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        supportedProviders.forEach {
            locationManager.addTestProviderSafe(
                it,
                supportsAltitude = true,
                supportsSpeed = true,
                supportsBearing = true,
                accuracy = defaultProviderAccuracy
            )
            locationManager.setTestProviderEnabledSafe(it, true)
        }
    }

    override fun mock(location: Location) {
        supportedProviders.forEach {
            val mockLocation = Location(location)
            mockLocation.provider = it
            locationManager.setTestProviderLocationSafe(it, mockLocation)
        }
    }

    override fun stop() {
        supportedProviders.forEach {
            locationManager.setTestProviderEnabledSafe(it, false)
            locationManager.removeTestProviderSafe(it)
        }
    }
}

private fun LocationManager.setTestProviderLocationSafe(provider: String, location: Location) =
    safeCall {
        setTestProviderLocation(provider, location)
    }

private fun LocationManager.setTestProviderEnabledSafe(provider: String, enabled: Boolean) =
    safeCall {
        setTestProviderEnabled(provider, enabled)
    }

private fun LocationManager.addTestProviderSafe(
    name: String,
    requiresNetwork: Boolean = false,
    requiresSatellite: Boolean = false,
    requiresCell: Boolean = false,
    hasMonetaryCost: Boolean = false,
    supportsAltitude: Boolean = false,
    supportsSpeed: Boolean = false,
    supportsBearing: Boolean = false,
    powerRequirement: Int = 0,
    accuracy: Int = 0
) = safeCall {
    addTestProvider(
        name,
        requiresNetwork,
        requiresSatellite,
        requiresCell,
        hasMonetaryCost,
        supportsAltitude,
        supportsSpeed,
        supportsBearing,
        powerRequirement,
        accuracy
    )
}

private fun LocationManager.removeTestProviderSafe(provider: String) = safeCall {
    removeTestProvider(provider)
}

private inline fun <T> safeCall(block: () -> T) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        block()
    }
}