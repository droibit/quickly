package com.droibit.quickly.packages

import android.app.IntentService
import android.content.Context
import android.content.Intent
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

    init {
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent) {
        Timber.d("onHandleIntent(intent=$intent)")
    }
}
