package com.droibit.quickly.main.apps

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.MainContract.QuickActionEvent
import com.droibit.quickly.main.apps.AppsContract.MenuItem
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.addTo
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class AppsPresenter(
        private val view: AppsContract.View,
        private val navigator: AppsContract.Navigator,
        private val loadTask: MainContract.LoadAppInfoTask,
        private val showSettingsTask: MainContract.ShowSettingsTask,
        private val subscriptions: CompositeSubscription) : AppsContract.Presenter {

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
    override fun onResume(forceLoad: Boolean) {
        loadTask.isRunning()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setLoadingIndicator(active = it) }
                .addTo(subscriptions)

        refreshApps(forceLoad)
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
            is MenuItem.Refresh -> refreshApps(forceLoad = true)
            is MenuItem.ShowSystem -> toggleShowSystemApps(menuItem.checked)
            is MenuItem.Settings -> navigator.navigateSettings()
        }
    }

    override fun onMoreItemClicked(app: AppInfo) {
        view.showQuickActionSheet(app)
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
    override fun onSearchButtonClicked() {
        navigator.navigateSearch()
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

    @UiThread
    override fun onQuickActionSelected(event: QuickActionEvent) {
        when (event) {
            is QuickActionEvent.Uninstall -> view.performUninstall(event.packageName)
            is QuickActionEvent.SharePackage -> view.performSharePackage(event.packageName)
            is QuickActionEvent.OpenAppInfo -> navigator.navigateAppInfoInSettings(event.packageName)
        }
    }

    private fun onAppsLoaded(apps: List<AppInfo>, reloaded: Boolean) {
        if (apps.isNotEmpty()) {
            view.showApps(apps, resetPosition = reloaded)
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