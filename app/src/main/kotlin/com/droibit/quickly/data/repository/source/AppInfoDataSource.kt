package com.droibit.quickly.data.repository.source

import com.droibit.quickly.data.repository.appinfo.AppInfo
import rx.Observable


interface AppInfoDataSource {

    fun getAll(): Observable<AppInfo>
}