package io.saeid.automator.location

import android.content.Context
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
        MockLocationAutomator.setProviders(stubProvider)
        mockLocation(mockLatitude, mockLongitude, preserve = true, preserveInterval = 1000)
        Thread.sleep(3500)
        assertEquals(4, stubProvider.mockCounts)
    }

    @Test
    fun verify_batch_location_updates() {
        val stubProvider = StubMockProvider()
        MockLocationAutomator.setProviders(stubProvider)
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
}
