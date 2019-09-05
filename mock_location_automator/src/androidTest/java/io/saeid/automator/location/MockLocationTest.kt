package io.saeid.automator.location

import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.location.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MockLocationTest {

    @get:Rule
    val mockLocationRule = MockLocationRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var client: FusedLocationProviderClient

    private val mockLatitude = 59.00
    private val mockLongitude = 38.00
    private val delta = 1e-15

    @Before
    fun setUp() {
        client = LocationServices.getFusedLocationProviderClient(context)
    }

    @Test
    fun verify_getLastKnownLocation_return_mock_location() {
        mockLocation(mockLatitude, mockLongitude)
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).forEach {
            val lastKnowLocation = context.locationManager().getLastKnownLocation(it)!!
            assertEquals(mockLatitude, lastKnowLocation.latitude, delta)
            assertEquals(mockLongitude, lastKnowLocation.longitude, delta)
        }
        client.getLastLocationSync().let {
            assertEquals(mockLatitude, it.latitude, delta)
            assertEquals(mockLongitude, it.longitude, delta)
        }
    }

    @Test
    fun verify_mocking_without_preserving_location() {
        mockLocation(mockLatitude, mockLongitude, preserve = false)
        var updateCounter = 0
        client.requestLocationUpdates(
            LocationRequest().setFastestInterval(1000).setInterval(1000 * 60 * 10).setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY
            ),
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    val it = p0!!.lastLocation!!
                    assertEquals(mockLatitude, it.latitude, delta)
                    assertEquals(mockLongitude, it.longitude, delta)
                    updateCounter++
                }
            }
            , Looper.getMainLooper()
        )
        Thread.sleep(5_000)
        assertEquals(1, updateCounter)
    }
}
