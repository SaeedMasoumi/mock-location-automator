package io.saeid.automator.location

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockLocationRule() : TestRule {

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
        TODO("Not implemented")
    }

    private fun denyMockLocationAccess(packageName: String) {
        TODO("Not implemented")
    }
}