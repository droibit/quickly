package com.droibit.quickly.packages

import com.droibit.quickly.packages.PackageContract.Action.*
import timber.log.Timber

class PackageActionHandler(private val receiver: PackageContract.Receiver)
    : PackageContract.ActionHandler {

    override fun onPackageAdded(packageName: String) {
        Timber.d("ACTION_PACKAGE_ADDED: $packageName")
        receiver.startPackageAction(PACKAGE_ADDED, packageName)
    }

    override fun onPackageReplaced(packageName: String) {
        Timber.d("ACTION_PACKAGE_REPLACED: $packageName")
        receiver.startPackageAction(PACKAGE_REPLACED, packageName)
    }

    override fun onPackageRemoved(packageName: String, replacing: Boolean) {
        Timber.d("ACTION_PACKAGE_REMOVED: $packageName, REPLACING=$replacing")

        if (!replacing) {
            receiver.startPackageAction(PACKAGE_REMOVED, packageName)
        }
    }
}