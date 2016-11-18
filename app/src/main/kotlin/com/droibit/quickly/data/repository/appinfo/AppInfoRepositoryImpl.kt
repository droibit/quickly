package com.droibit.quickly.data.repository.appinfo

import android.support.annotation.VisibleForTesting
import com.droibit.quickly.data.repository.source.AppInfoDataSource
import com.github.gfx.android.orma.annotation.OnConflict
import rx.Observable
import rx.Single
import java.util.*

class AppInfoRepositoryImpl(
        private val orma: OrmaDatabase,
        private val appInfoSource: AppInfoDataSource) : AppInfoRepository {

    @VisibleForTesting
    internal val cache: MutableMap<String, AppInfo> = LinkedHashMap()

    override fun loadAll(forceReload: Boolean): Observable<List<AppInfo>> {
        if (!forceReload && cache.isNotEmpty()) {
            return Observable.just(ArrayList(cache.values))
        }

        val storedAppInfo = getStoredAppInfo()
        if (forceReload) {
            return storedAppInfo
        }
        return Observable.concat(getLocalAppInfo(), storedAppInfo)
                .first { it.isNotEmpty() }
                .onErrorReturn { Collections.emptyList() }
    }

    override fun addOrUpdate(packageName: String): Single<Boolean> {
        return appInfoSource.get(packageName)
                .flatMap { appInfo ->
                    orma.prepareInsertIntoAppInfoAsObservable(OnConflict.REPLACE)
                            .flatMap { it.executeAsObservable(appInfo) }
                            .map { it > 0 }
                            .doOnSuccess { if (it) cache[packageName] = appInfo }
                }
    }

    override fun delete(packageName: String): Single<Boolean> {
        return orma.deleteFromAppInfo()
                .packageNameEq(packageName)
                .executeAsObservable()
                .map { it > 0 }
                .doOnSuccess { if (it) cache.remove(packageName) }
    }

    private fun getLocalAppInfo(): Observable<List<AppInfo>> {
        return orma.selectFromAppInfo()
                .executeAsObservable()
                .toList()
    }

    private fun getStoredAppInfo(): Observable<List<AppInfo>> {
        return appInfoSource.getAll()
                .doOnNext { storeSync(appInfoList = it) }
    }

    private fun storeSync(appInfoList: List<AppInfo>) {
        orma.transactionSync {
            orma.prepareInsertIntoAppInfo(OnConflict.REPLACE)
                    .executeAll(appInfoList)
        }

        cache.clear()
        appInfoList.forEach { cache[it.packageName] = it }
    }
}