package com.droibit.quickly.packages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import timber.log.Timber

class PackageActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        when (intent.action) {
            ACTION_PACKAGE_ADDED -> Timber.d("ACTION_PACKAGE_ADDED: $packageName")
            ACTION_PACKAGE_REPLACED -> Timber.d("ACTION_PACKAGE_REPLACED: $packageName, REPLACING=${intent.getBooleanExtra(EXTRA_REPLACING, false)}")
            ACTION_PACKAGE_REMOVED -> Timber.d("ACTION_PACKAGE_REMOVED: $packageName, REPLACING=${intent.getBooleanExtra(EXTRA_REPLACING, false)}")
            ACTION_MY_PACKAGE_REPLACED -> Timber.d("ACTION_MY_PACKAGE_REPLACED: $packageName")
        }
    }
}