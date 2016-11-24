package com.droibit.quickly.main.search

import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Single
import rx.lang.kotlin.singleOf

class ShowSettingsTask(private val showSettingsRepository: ShowSettingsRepository)
    : SearchContract.ShowSettingsTask {

    override fun load(): Single<Pair<SortBy, Order>> {
        return singleOf(Pair(showSettingsRepository.sortBy, showSettingsRepository.order))
    }
}