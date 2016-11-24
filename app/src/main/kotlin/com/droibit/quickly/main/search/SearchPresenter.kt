package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class SearchPresenter(
        private val view: SearchContract.View,
        private val showSettingsTask: SearchContract.ShowSettingsTask,
        private val sourceApps: List<AppInfo>) : SearchContract.Presenter {

    @UiThread
    override fun onCreate() {
        showSettingsTask.load()
                .observeOn(Schedulers.immediate())
                .subscribe { view.setSortBy(sortBy = it.first, order = it.second) }
    }

    @UiThread
    override fun onQueryTextEventEmitted(event: QueryTextEvent) {
        when (event) {
            is QueryTextEvent.Change -> {
                showFilteredApps(event.query)
            }
            is QueryTextEvent.Submit -> {
                view.closeSearch()
                showFilteredApps(event.query)
            }
        }
    }

    private fun showFilteredApps(query: String) {
        Timber.d("Search: $query")

        if (query.isEmpty()) {
            view.showApps(Collections.emptyList())
            return
        }

        val lowerQuery = query.toLowerCase()
        val filteredApps = sourceApps.filter {
            it.lowerName.contains(lowerQuery) || it.lowerPackageName.contains(lowerQuery)
        }
        view.showApps(filteredApps)
    }
}