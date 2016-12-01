package com.droibit.quickly.packages.service

import android.support.annotation.WorkerThread
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.packages.PackageContract

class PackageActionPerformer(
        private val appInfoRepository: AppInfoRepository,
        private val appEventBus: RxBus) : PackageContract.ActionPerformer {

    @WorkerThread
    override fun performPackageAction(action: PackageContract.Action, packageName: String) {

    }
}