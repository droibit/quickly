package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable
import rx.schedulers.Schedulers

class LoadAppInfoTask(
        private val appInfoRepository: AppInfoRepository,
        private val showSettingsRepository: ShowSettingsRepository,
        private val runningRelay: BehaviorRelay<Boolean>) : MainContract.LoadAppInfoTask {

    private var cachedApps: List<AppInfo>? = null

    @Suppress("HasPlatformType")
    override fun isRunning() = runningRelay.distinctUntilChanged()

    override fun requestLoad(forceReload: Boolean): Observable<List<AppInfo>> {
        // TODO: if forceReload, need delay 1sec
        return appInfoRepository.loadAll(forceReload)
                .map { apps -> apps.filter { filterIfOnlyInstalled(it) } }
                .filter { it != cachedApps }
                .doOnNext { cachedApps = it }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { runningRelay.call(true) }
                .doOnUnsubscribe { runningRelay.call(false) }

    }

    private fun filterIfOnlyInstalled(appInfo: AppInfo): Boolean {
        if (showSettingsRepository.isShowSystem) {
            return true
        }
        return !appInfo.preInstalled
    }
}