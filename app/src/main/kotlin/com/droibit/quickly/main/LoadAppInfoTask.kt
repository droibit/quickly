package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable
import rx.schedulers.Schedulers

class LoadAppInfoTask(
        private val appInfoRepository: AppInfoRepository,
        private val showSettingsRepository: ShowSettingsRepository,
        private val loadEventRelay: BehaviorRelay<LoadEvent>,
        private val runningRelay: BehaviorRelay<Boolean>) : MainContract.LoadAppInfoTask {

    override fun asObservable(): Observable<LoadEvent> = loadEventRelay

    @Suppress("HasPlatformType")
    override fun isRunning() = runningRelay.distinctUntilChanged()

    override fun requestLoad(forceReload: Boolean) {
        appInfoRepository.loadAll(forceReload)
                .map { apps -> LoadEvent.OnResult(apps.filter { filterIfOnlyInstalled(it) }) }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { runningRelay.call(true) }
                .doOnUnsubscribe { runningRelay.call(false) }
                .subscribe { loadEventRelay.call(it) }

    }

    private fun filterIfOnlyInstalled(appInfo: AppInfo): Boolean {
        if (showSettingsRepository.isShowSystem) {
            return true
        }
        return !appInfo.preInstalled
    }
}