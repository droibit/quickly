package com.droibit.quickly.main

import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Completable
import rx.Single

class ShowSettingsTask(private val showSettingsRepository: ShowSettingsRepository)
    : MainContract.ShowSettingsTask {

    override fun loadSortBy(): Single<Pair<SortBy, Order>> {
        return Single.just(Pair(showSettingsRepository.sortBy, showSettingsRepository.order))
    }

    override fun storeSortBy(sortBy: SortBy, order: Order): Single<Boolean> {
        return Single.fromCallable {
            val updated = showSettingsRepository.sortBy != sortBy || showSettingsRepository.order != order

            showSettingsRepository.sortBy = sortBy
            showSettingsRepository.order = order

            return@fromCallable updated
        }
    }

    override fun loadShowSystem(): Single<Boolean> {
        return Single.just(showSettingsRepository.isShowSystem)
    }

    override fun storeShowSystem(showSystem: Boolean): Completable {
        return Completable.fromAction {
            showSettingsRepository.isShowSystem = showSystem
        }
    }
}