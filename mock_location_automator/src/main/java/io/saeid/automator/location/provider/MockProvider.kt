package io.saeid.automator.location.provider

import android.content.Context
import android.location.Location

interface MockProvider {
    fun start(context: Context)
    fun mock(location: Location)
    fun stop()
}