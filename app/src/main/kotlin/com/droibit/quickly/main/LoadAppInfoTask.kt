package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.jakewharton.rxrelay.BehaviorRelay
import com.jakewharton.rxrelay.PublishRelay
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber

class LoadAppInfoTask(
        private val appInfoRepository: AppInfoRepository,
        private val showSettingsRepository: ShowSettingsRepository,
        private val runningRelay: BehaviorRelay<Boolean>) : MainContract.LoadAppInfoTask {

    private var cachedApps: List<AppInfo>? = null

    @Suppress("HasPlatformType")
    override fun isRunning() = runningRelay.distinctUntilChanged()

    override fun requestLoad(forceReload: Boolean): Observable<List<AppInfo>> {
        Timber.d("requestLoad(forceReload=$forceReload)")
        // TODO: if forceReload, need delay 1sec
        val shouldRunningCall = forceReload || !appInfoRepository.hasCache
        return appInfoRepository.loadAll(forceReload)
                .map { apps -> apps.filter { filterIfOnlyInstalled(it) } }
                .filter { it != cachedApps }
                .doOnNext { cachedApps = it }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { if (shouldRunningCall) runningRelay.call(true) } // TODO: need review
                .doOnUnsubscribe { if (shouldRunningCall) runningRelay.call(false) } // TODO: need review

    }

    private fun filterIfOnlyInstalled(appInfo: AppInfo): Boolean {
        if (showSettingsRepository.isShowSystem) {
            return true
        }
        return !appInfo.preInstalled
    }
}