package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Single

interface SearchContract {

    sealed class QueryTextEvent(val query: String) {
        class Change(query: String) : QueryTextEvent(query)
        class Submit(query: String) : QueryTextEvent(query)
    }

    interface View {

        fun setLoadingIndicator(active: Boolean)

        fun showApps(apps: List<AppInfo>)

        fun setSortBy(sortBy: SortBy, order: Order)

        fun closeSearch()
    }

    interface Presenter {

        @UiThread
        fun onCreate()

        @UiThread
        fun onQueryTextEventEmitted(event: QueryTextEvent)

        @UiThread
        fun onResume()

        @UiThread
        fun onPause()

        @UiThread
        fun onDestroy()

    }

    interface ShowSettingsTask {

        fun load(): Single<Pair<SortBy, Order>>
    }
}