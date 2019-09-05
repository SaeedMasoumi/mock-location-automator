package io.saeid.automator.location

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.FileInputStream
import java.io.InputStreamReader

class MockLocationRule(
    grantAccessFineLocationPermission: Boolean = true,
    grantAccessCoarseLocationPermission: Boolean = true
) : TestRule {

    private val permissions: List<String>

    init {
        permissions = ArrayList<String>().apply {
            add("android.permission.ACCESS_MOCK_LOCATION")
            if (grantAccessFineLocationPermission) {
                add("android.permission.ACCESS_FINE_LOCATION")
            }
            if (grantAccessCoarseLocationPermission) {
                add("android.permission.ACCESS_COARSE_LOCATION")
            }
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                // use the test apk context
                val context = InstrumentationRegistry.getInstrumentation().context

                before(context)
                try {
                    base.evaluate()
                } finally {
                    after(context)
                }
            }
        }
    }

    private fun before(context: Context) {
        grantNeededPermissions(context.packageName)
        grantMockLocationAccess(context.packageName)
        MockLocationAutomator.start(context)
    }

    private fun after(context: Context) {
        MockLocationAutomator.stop()
        revokeMockLocationAccess(context.packageName)
        revokeNeededPermissions(context.packageName)
    }

    private fun grantNeededPermissions(packageName: String) {
        permissions.forEach {
            executeCommand("pm grant $packageName $it", sync = true)

        }
    }

    private fun revokeNeededPermissions(packageName: String) {
        // "pm revoke" will cause the instrumentation test to crash
    }

    private fun grantMockLocationAccess(packageName: String) {
        executeCommand("appops set $packageName android:mock_location allow", sync = true)
    }

    private fun revokeMockLocationAccess(packageName: String) {
        executeCommand("appops set $packageName android:mock_location deny")
    }

    private fun executeCommand(command: String, sync: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with(InstrumentationRegistry.getInstrumentation().uiAutomation) {
                executeShellCommand(command).use { pfd ->
                    if (sync) {
                        // Synchronize with execution of command by reading the whole stream
                        InputStreamReader(FileInputStream(pfd.fileDescriptor)).readLines()
                    }
                }
            }
        }
    }
}