package com.droibit.quickly.main.search

import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Single

class ShowSettingsTask(private val showSettingsRepository: ShowSettingsRepository)
    : SearchContract.ShowSettingsTask {

    override fun load(): Single<Pair<SortBy, Order>> {
        return Single.just(Pair(showSettingsRepository.sortBy, showSettingsRepository.order))
    }
}