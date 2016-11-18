package com.droibit.quickly.data.repository.settings

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import java.util.*
import kotlin.comparisons.compareBy
import kotlin.comparisons.compareByDescending
import kotlin.comparisons.thenByDescending

object AppInfoComparators {

    private val comparators: MutableMap<String, Comparator<AppInfo>> = HashMap()

    fun get(sortBy: SortBy, order: Order): Comparator<AppInfo> {
        val key = "$sortBy-$order"
        return comparators[key] ?: create(sortBy, order).apply { comparators[key] = this }
    }

    private fun create(sortBy: SortBy, order: Order): Comparator<AppInfo> {
        return when (sortBy) {
            SortBy.NAME -> {
                if (order == Order.ASC) compareBy(AppInfo::name) else compareByDescending(AppInfo::name)
            }
            SortBy.LAST_UPDATED -> {
                val comparator = if (order == Order.ASC) compareBy(AppInfo::lastUpdateTime) else compareByDescending(AppInfo::lastUpdateTime)
                comparator.thenByDescending(AppInfo::name)
            }
            SortBy.PACKAGE -> {
                if (order == Order.ASC) compareBy(AppInfo::packageName) else compareByDescending(AppInfo::packageName)
            }
        }
    }
}