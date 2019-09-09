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
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        mockLocation(mockLatitude, mockLongitude)

        providers.forEach {
            val lastKnowLocation = context.locationManager().getLastKnownLocation(it)!!
            assertEquals(mockLatitude, lastKnowLocation.latitude, delta)
            assertEquals(mockLongitude, lastKnowLocation.longitude, delta)
        }
        client.getLastLocationSync().let {
            assertEquals(mockLatitude, it.latitude, delta)
            assertEquals(mockLongitude, it.longitude, delta)
        }
        // now mock a new location without preserving it
        mockLocation(mockLatitude + 1, mockLongitude + 1, preserve = false)

        providers.forEach {
            val lastKnowLocation = context.locationManager().getLastKnownLocation(it)!!
            assertEquals(mockLatitude + 1, lastKnowLocation.latitude, delta)
            assertEquals(mockLongitude + 1, lastKnowLocation.longitude, delta)
        }
        client.getLastLocationSync().let {
            assertEquals(mockLatitude + 1, it.latitude, delta)
            assertEquals(mockLongitude + 1, it.longitude, delta)
        }
    }

    @Test
    fun verify_location_updates_when_preserving_is_on() {
        val stubProvider = StubMockProvider()
        MockLocationAutomator.addProvider(stubProvider)
        mockLocation(mockLatitude, mockLongitude, preserve = true, preserveInterval = 1000)
        Thread.sleep(3500)
        assertEquals(4, stubProvider.mockCounts)
    }

    @Test
    fun verify_batch_location_updates() {
        val stubProvider = StubMockProvider()
        MockLocationAutomator.addProvider(stubProvider)
        val locations = ArrayList<DelayedLocation>()
        repeat(3) {
            locations.add(
                DelayedLocation(
                    latitude = mockLatitude,
                    longitude = mockLongitude,
                    delay = 500L * (it + 1)
                )
            )
        }
        mockLocations(locations)
        Thread.sleep(3500)
        assertEquals(3, stubProvider.mockCounts)
    }

    @Test
    fun verify_requestLocationUpdates_returns_mock_locations() {
        val request = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_NO_POWER
            interval = 1000
            fastestInterval = 1000
            smallestDisplacement = 0f
        }

        val locations = listOf(
            DelayedLocation(10.10, 20.20, delay = 2000),
            DelayedLocation(10.10, 20.21, delay = 2000),
            DelayedLocation(10.10, 20.22, delay = 2000),
            DelayedLocation(10.10, 20.23, delay = 2000)
        )
        mockLocations(locations)


        val latch = CountDownLatch(8)
        val retrievedLocations = HashSet<DelayedLocation>()

        client.requestLocationUpdates(request, object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                retrievedLocations.add(DelayedLocation(p0!!.lastLocation!!, 2000))
                latch.countDown()
            }
        }, Looper.getMainLooper())

        latch.await()

        locations.forEach { dl ->
            retrievedLocations.first {
                it.location.latitude == dl.location.latitude &&
                        it.location.longitude == dl.location.longitude
            }
        }
    }
}
