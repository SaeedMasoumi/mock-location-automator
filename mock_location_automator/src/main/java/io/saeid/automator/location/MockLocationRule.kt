package io.saeid.automator.location

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockLocationRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val idlingResource = MockLocationIdlingResource()
                val context = ApplicationProvider.getApplicationContext<Context>()

                before(context, idlingResource)
                base.evaluate()
                after(context, idlingResource)
            }
        }
    }

    private fun before(context: Context, idlingResource: MockLocationIdlingResource) {
        MockLocationAutomator.start(context)
        grantMockLocationAccess(context.packageName)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    private fun after(context: Context, idlingResource: MockLocationIdlingResource) {
        IdlingRegistry.getInstance().unregister(idlingResource)
        MockLocationAutomator.stop()
        denyMockLocationAccess(context.packageName)
    }

    private fun grantMockLocationAccess(packageName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with(InstrumentationRegistry.getInstrumentation().uiAutomation) {
                executeShellCommand("adb shell pm grant $packageName android.permission.ACCESS_MOCK_LOCATION")
                executeShellCommand("appops set $packageName android:mock_location allow")
            }
        }
    }

    private fun denyMockLocationAccess(packageName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with(InstrumentationRegistry.getInstrumentation().uiAutomation) {
                executeShellCommand("appops set $packageName android:mock_location deny")
            }
        }
    }
}