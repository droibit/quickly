package com.droibit.quickly.data.repository.appinfo

import rx.Observable
import rx.Single

interface AppInfoRepository {

    fun loadAll(forceReload: Boolean = false): Observable<List<AppInfo>>

    fun addOrUpdate(appInfo: AppInfo): Single<Boolean>

    fun delete(packageName: String): Single<Boolean>
}