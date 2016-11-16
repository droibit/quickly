package com.droibit.quickly.data.repository.settings

import com.chibatching.kotpref.KotprefModel
import com.droibit.quickly.data.config.ApplicationConfig
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy

class ShowSettingsRepositoryImpl(appConfig: ApplicationConfig) : KotprefModel(), ShowSettingsRepository {

    override val kotprefName = appConfig.showSettingsPrefsName

    override var isShowSystem: Boolean by booleanPrefVar(default = false)

    override var sortBy: SortBy by enumValuePrefVar(SortBy::class, default = SortBy.NAME)

    override var order: Order by enumValuePrefVar(Order::class, default = Order.DESC)
}