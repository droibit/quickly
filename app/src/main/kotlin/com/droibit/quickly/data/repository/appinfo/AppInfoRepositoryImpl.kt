package com.droibit.quickly.data.repository.appinfo

import com.droibit.quickly.data.repository.source.AppInfoDataSource
import rx.Observable
import rx.Single

class AppInfoRepositoryImpl(
        private val orma: OrmaDatabase,
        private val appInfoSource: AppInfoDataSource) : AppInfoRepository {

    override fun getAll(): Observable<List<AppInfo>> {
        // TODO: need review
        return orma.selectFromAppInfo()
                .executeAsObservable()
                .switchIfEmpty(storeAll())
                .toList()
    }

    override fun add(appInfo: AppInfo): Single<AppInfo> {
        TODO()
    }

    override fun update(appInfo: AppInfo): Single<Boolean> {
        TODO()
    }

    override fun delete(packageName: String): Single<Boolean> {
        TODO()
    }

    private fun storeAll(): Observable<AppInfo> {
        return appInfoSource.getAll()
                .flatMap {
                    orma.transactionSync {
                        val inserter = orma.prepareInsertIntoAppInfo()
                        inserter.executeAll(it)
                    }
                    return@flatMap Observable.from(it)
                }
    }
}