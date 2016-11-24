package com.droibit.quickly.data.repository.appinfo

import rx.Observable
import rx.Single

interface AppInfoRepository {

    val hasCache: Boolean

    fun loadAll(forceReload: Boolean = false): Observable<List<AppInfo>>

    fun addOrUpdate(packageName: String): Single<Boolean>

    fun delete(packageName: String): Single<Boolean>
}