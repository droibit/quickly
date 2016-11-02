package com.droibit.quickly.data.repository.source

import android.content.Context
import android.content.pm.ApplicationInfo.FLAG_SYSTEM
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.droibit.quickly.data.repository.appinfo.AppInfo
import rx.Observable

class AppInfoDataSourceImpl(private val context: Context,
                            private val selfPackageName: String) : AppInfoDataSource {

    override fun getAll(): Observable<AppInfo> {
        val pm = context.packageManager
        return Observable.from(pm.getInstalledPackages(0))
                .filter { selfPackageName != it.packageName }
                .map { it.toAppInfo(pm) }
    }

    private fun PackageInfo.toAppInfo(pm: PackageManager): AppInfo {
        return AppInfo(
                packageName = packageName,
                name = "${applicationInfo.loadLabel(pm)}",
                versionCode = versionCode,
                versionName = versionName,
                icon = applicationInfo.icon,
                preInstalled = (applicationInfo.flags and FLAG_SYSTEM) == 1,
                lastUpdateTime = lastUpdateTime
        )
    }
}