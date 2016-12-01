package com.droibit.quickly.packages.service

import android.support.annotation.WorkerThread
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfoContract
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.packages.PackageContract
import com.droibit.quickly.packages.PackageContract.Action.*
import rx.schedulers.Schedulers

class PackageActionPerformer(
        private val appInfoRepository: AppInfoRepository,
        private val appEventBus: RxBus) : PackageContract.ActionPerformer {

    @WorkerThread
    override fun performPackageAction(action: PackageContract.Action, packageName: String) {
        when (action) {
            PACKAGE_ADDED, PACKAGE_REPLACED -> addOrUpdateAppInfo(sourcePackage = packageName)
            PACKAGE_REMOVED -> deleteAppInfo(sourcePackage = packageName)
        }
    }

    private fun addOrUpdateAppInfo(sourcePackage: String) {
        appInfoRepository.addOrUpdate(packageName = sourcePackage)
                .observeOn(Schedulers.immediate())
                .subscribe { success ->
                    if (success && appEventBus.hasObservers) {
                        appEventBus.call(AppInfoContract.OnChangedEvent())
                    }
                }
    }

    private fun deleteAppInfo(sourcePackage: String) {
        appInfoRepository.delete(sourcePackage)
                .observeOn(Schedulers.immediate())
                .subscribe { success ->
                    if (success && appEventBus.hasObservers) {
                        appEventBus.call(AppInfoContract.OnChangedEvent())
                    }
                }
    }
}