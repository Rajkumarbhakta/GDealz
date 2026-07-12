package com.rkbapps.gdealz.util

import androidx.lifecycle.LifecycleObserver

object AppForegroundTracker : LifecycleObserver {
    @Volatile
    private var isInForeground = false

    fun isAppInForeground(): Boolean = isInForeground

    fun setAppInForeground(foreground: Boolean) {
        isInForeground = foreground
    }

}
