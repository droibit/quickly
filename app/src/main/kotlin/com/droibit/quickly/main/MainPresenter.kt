package com.droibit.quickly.main

import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription

class MainPresenter(
        private val view: MainContract.View,
        private val loadTask: MainContract.LoadAppInfoTask,
        private val subscriptions: CompositeSubscription) : MainContract.Presenter {

    private var cacheEvent: LoadEvent? = null

    override fun onCreate(shouldLoad: Boolean) {
        loadTask.requestLoad(shouldLoad)
    }

    override fun onResume() {
        loadTask.isRunning()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setLoadingIndicator(active = it) }
                .addTo(subscriptions)

        loadTask.asObservable()
                .filter { it !== cacheEvent }
                .doOnNext { cacheEvent = it }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onLoadAppInfos(event = it) }
                .addTo(subscriptions)
    }

    override fun onPause() {
        subscriptions.clear()
    }

    override fun onOptionsItemClicked(menuItem: MainContract.MenuItem) {
    }

    private fun onLoadAppInfos(event: LoadEvent) {
        when (event) {
            is LoadEvent.OnResult -> {
                if (event.appInfos.isNotEmpty()) {
                    view.showAppInfoList(event.appInfos)
                } else {
                    view.showNoAppInfo()
                }
            }
        }
        cacheEvent = event
    }
}