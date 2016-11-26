package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import rx.Completable
import rx.Observable
import rx.Single

interface MainContract {

    interface LoadAppInfoTask {

        fun isRunning(): Observable<Boolean>

        fun requestLoad(forceReload: Boolean = false): Observable<List<AppInfo>>
    }

    interface ShowSettingsTask {

        fun loadSortBy(): Single<Pair<ShowSettingsRepository.SortBy, ShowSettingsRepository.Order>>

        fun storeSortBy(sortBy: ShowSettingsRepository.SortBy, order: ShowSettingsRepository.Order): Single<Boolean>

        fun loadShowSystem(): Single<Boolean>

        fun storeShowSystem(showSystem: Boolean): Completable
    }

    sealed class QuickActionEvent(val app: AppInfo) {
        class Uninstall(app: AppInfo) : QuickActionEvent(app)
        class SharePackage(app: AppInfo) : QuickActionEvent(app)
    }
}