package com.droibit.quickly.main

import com.droibit.quickly.data.config.ApplicationConfig
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class LoadAppInfoTask(
        private val appInfoRepository: AppInfoRepository,
        private val showSettingsRepository: ShowSettingsRepository,
        private val appConfig: ApplicationConfig,
        private val appInfosRelay: BehaviorRelay<LoadEvent>,
        private val runningRelay: BehaviorRelay<Boolean>) : MainContract.LoadAppInfoTask {

    override fun asObservable(): Observable<LoadEvent> = appInfosRelay

    @Suppress("HasPlatformType")
    override fun isRunning() = runningRelay.distinctUntilChanged()

    override fun requestLoad(forceReload: Boolean) {
        appInfoRepository.loadAll(forceReload)
                .onErrorReturn { Collections.emptyList() }
                .timestamp()
                .flatMap {
                    Timber.d("LoadAppInfo: ${it.timestampMillis}ms")
                    return@flatMap Observable.from(it.value)
                            .filter { filterIfOnlyInstalled(appInfo = it) }
                            // TODO: sort
                            .toList()
                            .run {
                                val elapsedTime = appConfig.minTaskDurationMillis - it.timestampMillis
                                if (elapsedTime > 0) delay(elapsedTime, TimeUnit.MILLISECONDS, Schedulers.immediate()) else this
                            }
                }
                .map { LoadEvent.OnResult(appInfos = it) }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { runningRelay.call(true) }
                .doOnUnsubscribe { runningRelay.call(false) }
                .subscribe { appInfosRelay.call(it) }
    }

    private fun filterIfOnlyInstalled(appInfo: AppInfo): Boolean {
        if (showSettingsRepository.isShowSystem) {
            return true
        }
        return !appInfo.preInstalled
    }
}