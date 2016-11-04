package com.droibit.quickly.data.repository.appinfo

import rx.Observable
import rx.Single

interface AppInfoRepository {

    fun getAll(): Observable<List<AppInfo>>

    fun addOrUpdate(appInfo: AppInfo): Single<Boolean>

    fun delete(packageName: String): Single<Boolean>
}