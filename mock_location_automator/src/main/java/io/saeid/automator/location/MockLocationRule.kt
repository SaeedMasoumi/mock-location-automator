package io.saeid.automator.location

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockLocationRule : TestRule {

    private val context: Context by lazy { ApplicationProvider.getApplicationContext() }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                grantMockLocationAccess(context.packageName)
                MockLocationAutomator.start(context)
                base.evaluate()
                MockLocationAutomator.stop()
                denyMockLocationAccess(context.packageName)
            }
        }
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