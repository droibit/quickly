package com.droibit.quickly.data.repository.source

import com.droibit.quickly.data.repository.appinfo.AppInfo
import rx.Observable
import rx.Single


interface AppInfoDataSource {

    fun getAll(): Observable<AppInfo>

    fun get(packageName: String): Single<AppInfo>
}