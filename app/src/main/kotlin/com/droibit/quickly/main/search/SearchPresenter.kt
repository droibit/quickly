package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import android.support.annotation.VisibleForTesting
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.main.LoadAppInfoTask
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*

class SearchPresenter(
        private val view: SearchContract.View,
        private val loadTask: MainContract.LoadAppInfoTask,
        private val showSettingsTask: MainContract.ShowSettingsTask,
        private val subscriptions: CompositeSubscription,
        private val sourceApps: MutableList<AppInfo>) : SearchContract.Presenter {

    @UiThread
    override fun onCreate() {
        showSettingsTask.loadSortBy()
                .observeOn(Schedulers.immediate())
                .subscribe { view.setSortBy(sortBy = it.first, order = it.second) }
    }

    override fun onResume() {
        loadTask.isRunning()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setLoadingIndicator(active = it) }
                .addTo(subscriptions)

        loadApps()
    }

    override fun onPause() {
        subscriptions.clear()
    }

    override fun onDestroy() {
        subscriptions.unsubscribe()
    }

    @UiThread
    override fun onQueryTextEventEmitted(event: QueryTextEvent) {
        when (event) {
            is QueryTextEvent.Change -> showFilteredApps(event.query)
            is QueryTextEvent.Submit -> view.closeSearch()
        }
    }

    // Private

    private fun loadApps() {
        loadTask.requestLoad()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onAppLoaded(apps = it) }
                .addTo(subscriptions)
    }

    private fun onAppLoaded(apps: List<AppInfo>) {
        if (sourceApps.isNotEmpty()) {
            sourceApps.clear()
        }
        sourceApps.addAll(apps)
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