package com.droibit.quickly.packages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import com.droibit.quickly.BuildConfig
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance

class PackageActionReceiver : BroadcastReceiver() {

    private val injector = KodeinInjector()

    private val actionHandler: PackageContract.ActionHandler by injector.instance()

    override fun onReceive(context: Context, intent: Intent) {
        injector.inject(Kodein {
            extend(context.appKodein())
            import(packageModule())
        })

        when (intent.action) {
            ACTION_PACKAGE_ADDED -> {
                actionHandler.onPackageAdded(intent.packageName)
            }
            ACTION_PACKAGE_REPLACED -> {
                actionHandler.onPackageReplaced(intent.packageName)
            }
            ACTION_PACKAGE_REMOVED -> {
                actionHandler.onPackageRemoved(intent.packageName,
                        replacing = intent.getBooleanExtra(EXTRA_REPLACING, false))
            }
            ACTION_MY_PACKAGE_REPLACED -> {
                actionHandler.onPackageReplaced(packageName = BuildConfig.APPLICATION_ID)
            }
        }
    }

    private val Intent.packageName: String
        get() = data.schemeSpecificPart
}