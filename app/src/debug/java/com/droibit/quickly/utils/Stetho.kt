package com.droibit.quickly.utils

import android.app.Application
import com.facebook.stetho.Stetho as RawStetho


object Stetho {

    fun initialize(application: Application) {
        RawStetho.initializeWithDefaults(application)
    }
}