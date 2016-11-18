package com.droibit.quickly.data.repository.settings

interface ShowSettingsRepository {

    enum class SortBy(val index: Int) {
        NAME(index = 0),
        PACKAGE(index = 1),
        LAST_UPDATED(index = 2);

        companion object {
            @JvmStatic
            fun from(index: Int) = values().first { it.index == index }
        }
    }

    enum class Order {
        ASC, DESC
    }

    var isShowSystem: Boolean

    var sortBy: SortBy

    var order: Order
}