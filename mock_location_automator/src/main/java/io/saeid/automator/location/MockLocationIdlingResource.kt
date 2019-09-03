package io.saeid.automator.location

import androidx.test.espresso.IdlingResource

class MockLocationIdlingResource : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null
    private var isIdle = true

    override fun getName(): String = javaClass.simpleName

    override fun isIdleNow(): Boolean = isIdle

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}