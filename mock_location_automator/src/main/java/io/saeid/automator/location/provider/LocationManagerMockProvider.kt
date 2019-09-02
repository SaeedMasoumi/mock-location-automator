package io.saeid.automator.location.provider

import android.content.Context
import android.location.Location
import android.location.LocationManager

internal class LocationManagerMockProvider : MockProvider {

    private lateinit var locationManager: LocationManager
    private val supportedProviders = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
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

private fun LocationManager.setTestProviderLocationSafe(provider: String, location: Location) {
    try {
        setTestProviderLocation(provider, location)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun LocationManager.setTestProviderEnabledSafe(provider: String, enabled: Boolean) {
    try {
        setTestProviderEnabled(provider, enabled)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun LocationManager.addTestProviderSafe(
    name: String, requiresNetwork: Boolean = false, requiresSatellite: Boolean = false,
    requiresCell: Boolean = false, hasMonetaryCost: Boolean = false, supportsAltitude: Boolean = false,
    supportsSpeed: Boolean = false, supportsBearing: Boolean = false, powerRequirement: Int = 0, accuracy: Int = 0
) {
    try {
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
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun LocationManager.removeTestProviderSafe(provider: String) {
    try {
        removeTestProvider(provider)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}