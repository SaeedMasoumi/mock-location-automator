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
import java.util.concurrent.CountDownLatch

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LocationManagerMockTest {

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
}
