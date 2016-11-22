package com.droibit.quickly.main

import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Single

class SortByTask(private val showSettingsRepository: ShowSettingsRepository)
    : MainContract.SortByTask {

    override fun load(): Single<Pair<SortBy, Order>> {
        return Single.just(Pair(showSettingsRepository.sortBy, showSettingsRepository.order))
    }

    override fun store(sortBy: SortBy, order: Order): Single<Boolean> {
        return Single.fromCallable {
            val updated = showSettingsRepository.sortBy != sortBy || showSettingsRepository.order != order

            showSettingsRepository.sortBy = sortBy
            showSettingsRepository.order = order

            return@fromCallable updated
        }
    }
}