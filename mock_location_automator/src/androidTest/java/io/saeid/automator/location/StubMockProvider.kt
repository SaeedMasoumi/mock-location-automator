package io.saeid.automator.location

import android.content.Context
import android.location.Location
import io.saeid.automator.location.provider.MockProvider

class StubMockProvider : MockProvider {
    var mockCounts: Int = 0
    override fun start(context: Context) {
        // no-op
    }

    override fun mock(location: Location) {
        mockCounts++
    }

    override fun stop() {
        // no-op
    }
}