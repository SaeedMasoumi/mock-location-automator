package io.saeid.automator.location

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.concurrent.CountDownLatch

fun Context.locationManager() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

fun FusedLocationProviderClient.getLastLocationSync(): Location {
    val latch = CountDownLatch(1)
    var ll: Location? = null
    lastLocation.addOnSuccessListener {
        if (it != null) {
            ll = it
            latch.countDown()
        }
    }
    latch.await()
    return ll!!
}