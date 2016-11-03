package com.droibit.quickly.data.repository.appinfo

import rx.Observable
import rx.Single

interface AppInfoRepository {

    fun getAll(): Observable<AppInfo>

    fun addAll(appInfoList: List<AppInfo>): Observable<AppInfo>

    fun add(appInfo: AppInfo): Single<AppInfo>

    fun update(appInfo: AppInfo): Single<Boolean>

    fun delete(packageName: String): Single<Boolean>
}