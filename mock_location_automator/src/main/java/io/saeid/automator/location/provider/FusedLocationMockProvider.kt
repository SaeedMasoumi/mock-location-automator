package io.saeid.automator.location.provider

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
internal class FusedLocationMockProvider : MockProvider {
    private lateinit var client: FusedLocationProviderClient

    override fun start(context: Context) {
        client = LocationServices.getFusedLocationProviderClient(context)
        client.setMockMode(true)
    }

    override fun mock(location: Location) {
        val locationFromNetwork = Location(location)
        locationFromNetwork.provider = LocationManager.NETWORK_PROVIDER

        client.setMockLocation(location) // gps location
        client.setMockLocation(locationFromNetwork)
    }

    override fun stop() {
        client.setMockMode(false)
    }
}