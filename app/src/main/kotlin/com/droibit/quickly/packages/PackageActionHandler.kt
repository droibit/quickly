package com.droibit.quickly.packages

import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import timber.log.Timber

class PackageActionHandler(
        private val appInfoRepository: AppInfoRepository,
        private val appEventBus: RxBus) : PackageContract.ActionHandler {

    override fun onPackageAdded(packageName: String) {
        Timber.d("ACTION_PACKAGE_ADDED: $packageName")
        //TODO()
    }

    override fun onPackageReplaced(packageName: String) {
        Timber.d("ACTION_PACKAGE_REMOVED: $packageName")
        //TODO()
    }

    override fun onPackageRemoved(packageName: String, replacing: Boolean) {
        Timber.d("ACTION_PACKAGE_REPLACED: $packageName, REPLACING=$replacing")
        //TODO()
    }
}