package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract
import rx.Single

interface SearchContract {

    sealed class QueryTextEvent(val query: String) {
        class Change(query: String) : QueryTextEvent(query)
        class Submit(query: String) : QueryTextEvent(query)
    }

    interface View {

        val searchQuery: String

        fun setLoadingIndicator(active: Boolean)

        fun showApps(apps: List<AppInfo>)

        fun showNoApps()

        fun setSortBy(sortBy: SortBy, order: Order)

        fun closeSearch()

        fun  showQuickActionSheet(app: AppInfo)

        fun  performUninstall(packageName: String)

        fun  performSharePackage(packageName: String)
    }

    interface Navigator {

        fun navigateAppInfoInSettings(packageName: String)
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

        @UiThread
        fun onMoreItemClicked(app: AppInfo)

        @UiThread
        fun  onQuickActionSelected(event: MainContract.QuickActionEvent)

        @UiThread
        fun onPackageChanged()
    }

    interface ShowSettingsTask {

        fun load(): Single<Pair<SortBy, Order>>
    }
}