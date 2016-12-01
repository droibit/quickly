package com.droibit.quickly.packages.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import com.droibit.quickly.packages.PackageActionService
import com.droibit.quickly.packages.PackageContract
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance

class PackageActionReceiver : BroadcastReceiver(), PackageContract.Receiver {

    private val injector = KodeinInjector()

    private val actionHandler: PackageContract.ActionHandler by injector.instance()

    private val context: Context by injector.instance()

    override fun onReceive(context: Context, intent: Intent) {
        injector.inject(Kodein {
            import(packageModule(context, receiver = this@PackageActionReceiver))
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
//            ACTION_MY_PACKAGE_REPLACED -> {
//                actionHandler.onPackageReplaced(packageName = BuildConfig.APPLICATION_ID)
//            }
        }
    }

    override fun startPackageAction(action: PackageContract.Action, packageName: String) {
        val intent = PackageActionService.newIntent(context, action, packageName)
        context.startService(intent)
    }
}

private val Intent.packageName: String
    get() = data.schemeSpecificPart