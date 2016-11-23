package com.droibit.quickly.main

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.MenuItem
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class MainPresenter(
        private val view: MainContract.View,
        private val navigator: MainContract.Navigator,
        private val loadTask: MainContract.LoadAppInfoTask,
        private val showSettingsTask: MainContract.ShowSettingsTask,
        private val subscriptions: CompositeSubscription) : MainContract.Presenter {

    @UiThread
    override fun onCreate(shouldLoad: Boolean) {
        showSettingsTask.loadSortBy()
                .observeOn(Schedulers.immediate())
                .subscribe {
                    val (sortBy, order) = it
                    view.setSortBy(sortBy, order)
                }
    }

    @UiThread
    override fun onResume() {
        loadTask.isRunning()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setLoadingIndicator(active = it) }
                .addTo(subscriptions)

        refreshApps(forceLoad = false)
    }

    @UiThread
    override fun onPause() {
        subscriptions.clear()
    }

    @UiThread
    override fun onDestroy() {
        subscriptions.unsubscribe()
    }

    @UiThread
    override fun onPrepareShowSystemMenu() {
        showSettingsTask.loadShowSystem()
                .subscribeOn(Schedulers.immediate())
                .subscribe { view.setShowSystem(showSystem = it) }
    }

    @UiThread
    override fun onOptionsItemClicked(menuItem: MenuItem) {
        when (menuItem) {
            is MenuItem.Search -> navigator.navigateSearch(sourceApps = menuItem.apps)
            is MenuItem.Refresh -> refreshApps(forceLoad = true)
            is MenuItem.ShowSystem -> toggleShowSystemApps(menuItem.checked)
        }
    }

    @UiThread
    override fun onSortByClicked() {
        showSettingsTask.loadSortBy()
                .subscribeOn(Schedulers.immediate())
                .subscribe {
                    view.showSortByChooserDialog(sortBy = it.first)
                }
    }

    @UiThread
    override fun onSortByChoose(sortBy: SortBy, order: Order) {
        showSettingsTask.storeSortBy(sortBy, order)
                .subscribeOn(Schedulers.immediate())
                .subscribe { updated ->
                    if (updated) {
                        view.setSortBy(sortBy, order)
                    }
                    Timber.d("Sort by updated: $updated, $sortBy, $order")
                }
    }

    private fun onAppsLoaded(apps: List<AppInfo>, reloaded: Boolean) {
        if (apps.isNotEmpty()) {
            view.showAppInfoList(apps, resetPosition = reloaded)
        } else {
            view.showNoAppInfo()
        }
    }

    private fun toggleShowSystemApps(checked: Boolean) {
        showSettingsTask.storeShowSystem(checked)
                .andThen(loadTask.requestLoad())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onAppsLoaded(apps = it, reloaded = true) }
    }

    private fun refreshApps(forceLoad: Boolean) {
        loadTask.requestLoad(forceLoad)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onAppsLoaded(apps = it, reloaded = forceLoad) }
                .addTo(subscriptions)
    }
}