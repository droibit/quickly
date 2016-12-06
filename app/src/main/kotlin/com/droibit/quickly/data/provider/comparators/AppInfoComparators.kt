package com.droibit.quickly.data.provider.comparators

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import java.util.*
import kotlin.comparisons.compareBy
import kotlin.comparisons.compareByDescending
import kotlin.comparisons.thenBy

object AppInfoComparators {

    private val comparators: MutableMap<String, Comparator<AppInfo>> = HashMap()

    fun get(sortBy: SortBy, order: Order): Comparator<AppInfo> {
        val key = "$sortBy-$order"
        return comparators[key] ?: create(sortBy, order).apply { comparators[key] = this }
    }

    private fun create(sortBy: SortBy, order: Order): Comparator<AppInfo> {
        return when (sortBy) {
            SortBy.NAME -> {
                if (order == Order.ASC)
                    compareBy(AppInfo::lowerName)
                else
                    compareByDescending(AppInfo::lowerName)
            }
            SortBy.LAST_UPDATED -> {
                if (order == Order.ASC)
                    compareBy(AppInfo::lastUpdateTime).thenBy(AppInfo::lowerName)
                else
                    compareByDescending(AppInfo::lastUpdateTime).thenBy(AppInfo::lowerName)
            }
            SortBy.PACKAGE -> {
                if (order == Order.ASC)
                    compareBy(AppInfo::lowerPackageName)
                else
                    compareByDescending(AppInfo::lowerPackageName)
            }
        }
    }
}