package com.droibit.quickly.main

import com.droibit.quickly.data.config.ApplicationConfig
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class LoadAppInfoTask(
        private val appInfoRepository: AppInfoRepository,
        private val appConfig: ApplicationConfig,
        private val appInfosRelay: BehaviorRelay<LoadEvent>,
        private val runningRelay: BehaviorRelay<Boolean>) : MainContract.LoadAppInfoTask {

    override fun asObservable(): Observable<LoadEvent> = appInfosRelay

    @Suppress("HasPlatformType")
    override fun isRunning() = runningRelay.distinctUntilChanged()

    override fun load(forceReload: Boolean) {
        appInfoRepository.loadAll(forceReload)
                .onErrorReturn { Collections.emptyList() }
                .timestamp()
                .flatMap {
                    return@flatMap Observable.just(it).run {
                        val elapsedTime = appConfig.minTaskDurationMillis - it.timestampMillis
                        if (elapsedTime > 0) delay(elapsedTime, TimeUnit.MILLISECONDS, Schedulers.immediate()) else this
                    }
                }
                .map { LoadEvent.OnResult(it.value) }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { runningRelay.call(true) }
                .doOnUnsubscribe { runningRelay.call(false) }
                .subscribe { appInfosRelay.call(it) }
    }
}