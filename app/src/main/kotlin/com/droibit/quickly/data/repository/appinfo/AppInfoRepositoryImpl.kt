package com.droibit.quickly.data.repository.appinfo

import android.support.annotation.VisibleForTesting
import com.droibit.quickly.data.repository.source.AppInfoDataSource
import com.github.gfx.android.orma.annotation.OnConflict
import rx.Observable
import rx.Single
import rx.lang.kotlin.toSingletonObservable
import java.util.*

class AppInfoRepositoryImpl(
        private val orma: OrmaDatabase,
        private val appInfoSource: AppInfoDataSource) : AppInfoRepository {

    @VisibleForTesting
    internal val cache: MutableMap<String, AppInfo> = LinkedHashMap()

    // TODO: need lock
    override val hasCache: Boolean
        get() = cache.isNotEmpty()

    override fun loadAll(forceReload: Boolean): Observable<List<AppInfo>> {
        if (!forceReload && cache.isNotEmpty()) {
            return cache.values.toList().toSingletonObservable()
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
                            .doOnSuccess { if (cache.isNotEmpty()) cache.clear() }
                }
    }

    override fun delete(packageName: String): Single<Boolean> {
        return orma.deleteFromAppInfo()
                .packageNameEq(packageName)
                .executeAsObservable()
                .map { it > 0 }
                .doOnSuccess { if (cache.isNotEmpty()) cache.clear() }
    }

    private fun getLocalAppInfo(): Observable<List<AppInfo>> {
        return orma.selectFromAppInfo()
                .executeAsObservable()
                .doOnNext { cache[it.packageName] = it }
                .toList()
    }

    private fun getStoredAppInfo(): Observable<List<AppInfo>> {
        return appInfoSource.getAll()
                .doOnNext { cache[it.packageName] = it }
                .toList()
                .doOnNext { storeSync(appInfoList = it) }
    }

    private fun storeSync(appInfoList: List<AppInfo>) {
        orma.transactionSync {
            orma.prepareInsertIntoAppInfo(OnConflict.REPLACE)
                    .executeAll(appInfoList)
        }
    }
}