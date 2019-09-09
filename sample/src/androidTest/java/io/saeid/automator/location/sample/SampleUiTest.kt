package io.saeid.automator.location.sample

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import io.saeid.automator.location.DelayedLocation
import io.saeid.automator.location.MockLocationRule
import io.saeid.automator.location.mockLocations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SampleUiTest {

    @get:Rule
    val mockLocationRule = MockLocationRule()

    @get:Rule
    val activity = ActivityTestRule(MapsActivity::class.java)

    @Test
    fun just_a_sample_of_mockLocation_usage() {
        mockLocations(
            DelayedLocation(40.782055, -73.973238, delay = 0),
            DelayedLocation(40.781528, -73.973571, delay = 1000),
            DelayedLocation(40.781057, -73.973732, delay = 1000),
            DelayedLocation(40.780373, -73.972689, delay = 1000),
            DelayedLocation(40.781057, -73.973732, delay = 1000),
            DelayedLocation(40.781528, -73.973571, delay = 1000),
            DelayedLocation(40.782055, -73.973238, delay = 1000)
        )

        Thread.sleep(30_000)
    }
}
