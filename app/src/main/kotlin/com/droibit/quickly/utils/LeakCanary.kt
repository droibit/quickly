package com.droibit.quickly.utils

import android.app.Application
import com.squareup.leakcanary.LeakCanary as RawLeakCanary


object LeakCanary {

    fun initialize(application: Application) {
        if (RawLeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        RawLeakCanary.install(application)
    }
}