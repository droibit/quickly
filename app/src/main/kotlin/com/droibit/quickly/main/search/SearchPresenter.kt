package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import android.support.annotation.VisibleForTesting
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.main.LoadAppInfoTask
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.MainContract.QuickActionEvent
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*

class SearchPresenter(
        private val view: SearchContract.View,
        private val navigator: SearchContract.Navigator,
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
            is QueryTextEvent.Change -> showFilteredApps(event.query, loaded = false)
            is QueryTextEvent.Submit -> view.closeSearch()
        }
    }

    @UiThread
    override fun onMoreItemClicked(app: AppInfo) {
        view.showQuickActionSheet(app)
    }

    override fun onQuickActionSelected(event: QuickActionEvent) {
        when (event) {
            is QuickActionEvent.Uninstall -> view.performUninstall(event.packageName)
            is QuickActionEvent.SharePackage -> view.performSharePackage(event.packageName)
            is QuickActionEvent.OpenAppInfo -> navigator.navigateAppInfoInSettings(event.packageName)
        }
    }

    override fun onPackageChanged() {
        loadApps()
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

        showFilteredApps(view.searchQuery, loaded = true)
    }

    private fun showFilteredApps(query: String, loaded: Boolean) {
        Timber.d("Search: $query")

        if (query.isEmpty()) {
            view.showNoApps()
            return
        }

        val lowerQuery = query.toLowerCase()
        val filteredApps = sourceApps.filter {
            it.lowerName.contains(lowerQuery) || it.lowerPackageName.contains(lowerQuery)
        }
        if (filteredApps.isEmpty()) {
            view.showNoApps()
        } else {
            view.showApps(filteredApps, resetPosition = !loaded)
        }
    }
}