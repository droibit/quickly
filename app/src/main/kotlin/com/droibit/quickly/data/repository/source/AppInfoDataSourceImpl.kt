package com.droibit.quickly.data.repository.source

import android.content.pm.ApplicationInfo.FLAG_SYSTEM
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.droibit.quickly.data.repository.appinfo.AppInfo
import rx.Observable
import rx.Single

class AppInfoDataSourceImpl(private val pm: PackageManager) : AppInfoDataSource {

    override fun getAll(): Observable<AppInfo> {
        return Observable.from(pm.getInstalledPackages(0))
                .map { it.toAppInfo() }
    }

    override fun get(packageName: String): Single<AppInfo> {
        return Single.just(pm.getPackageInfo(packageName, 0))
                .map { it.toAppInfo() }
    }

    private fun PackageInfo.toAppInfo(): AppInfo {
        return AppInfo(
                packageName = packageName,
                name = "${applicationInfo.loadLabel(pm)}",
                versionCode = versionCode,
                versionName = versionName ?: "(NULL)",
                icon = applicationInfo.icon,
                preInstalled = (applicationInfo.flags and FLAG_SYSTEM) == 1,
                lastUpdateTime = lastUpdateTime
        )
    }
}