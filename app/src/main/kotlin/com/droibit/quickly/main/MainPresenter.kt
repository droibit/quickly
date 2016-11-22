package com.droibit.quickly.main

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
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

    private var cacheEvent: LoadEvent? = null

    @UiThread
    override fun onCreate(shouldLoad: Boolean) {
        sortByTask.load()
                .observeOn(Schedulers.immediate())
                .subscribe {
                    val (sortBy, order) = it
                    view.setSortBy(sortBy, order)
                }
        loadTask.requestLoad(shouldLoad)
    }

    @UiThread
    override fun onResume() {
        loadTask.isRunning()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setLoadingIndicator(active = it) }
                .addTo(subscriptions)

        loadTask.asObservable()
                .filter { it !== cacheEvent }
                .doOnNext { cacheEvent = it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onAppsLoaded(event = it) }
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

    private fun onAppsLoaded(event: LoadEvent) {
        when (event) {
            is LoadEvent.OnResult -> {
                if (event.apps.isNotEmpty()) {
                    view.showAppInfoList(event.apps)
                } else {
                    view.showNoAppInfo()
                }
            }
        }
        cacheEvent = event
    }
}