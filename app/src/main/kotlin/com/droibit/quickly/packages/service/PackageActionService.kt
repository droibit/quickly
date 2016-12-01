package com.droibit.quickly.packages.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.droibit.quickly.packages.PackageContract
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import timber.log.Timber


class PackageActionService : IntentService(PackageActionService::class.java.simpleName) {

    companion object {

        @JvmStatic
        fun newIntent(context: Context, action: PackageContract.Action, packageName: String): Intent {
            return Intent(context, PackageActionService::class.java)
                    .putExtra(EXTRA_ACTION, action)
                    .putExtra(EXTRA_PACKAGE_NAME, packageName)
        }

        private val EXTRA_ACTION = "EXTRA_ACTION"

        private val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
    }

    private val injector = KodeinInjector()

    private val performer: PackageContract.ActionPerformer by injector.instance()

    init {
        injector.inject(Kodein {
            extend(appKodein())
            import(packageModule())
        })
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent) {
        Timber.d("onHandleIntent(intent=$intent)")

        performer.performPackageAction(
                action = intent.getSerializableExtra(EXTRA_ACTION) as PackageContract.Action,
                packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        )
    }
}
