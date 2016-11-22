package com.droibit.quickly.main

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class MainPresenter(
        private val view: MainContract.View,
        private val loadTask: MainContract.LoadAppInfoTask,
        private val sortByTask: MainContract.SortByTask,
        private val subscriptions: CompositeSubscription) : MainContract.Presenter {

    @UiThread
    override fun onCreate(shouldLoad: Boolean) {
        sortByTask.load()
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

        loadTask.requestLoad()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onAppsLoaded(apps = it) }
                .addTo(subscriptions)
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
    override fun onOptionsItemClicked(menuItem: MainContract.MenuItem) {
    }

    @UiThread
    override fun onSortByClicked() {
        sortByTask.load()
                .subscribeOn(Schedulers.immediate())
                .subscribe {
                    view.showSortByChooserDialog(sortBy = it.first)
                }
    }

    @UiThread
    override fun onSortByChoose(sortBy: SortBy, order: Order) {
        sortByTask.store(sortBy, order)
                .subscribeOn(Schedulers.immediate())
                .subscribe { updated ->
                    if (updated) {
                        view.setSortBy(sortBy, order)
                    }
                    Timber.d("Sort by updated: $updated, $sortBy, $order")
                }
    }

    private fun onAppsLoaded(apps: List<AppInfo>) {
        if (apps.isNotEmpty()) {
            view.showAppInfoList(apps)
        } else {
            view.showNoAppInfo()
        }
    }
}