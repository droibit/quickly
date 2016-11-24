package com.droibit.quickly.main

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Completable
import rx.Observable
import rx.Single

interface MainContract {

    sealed class MenuItem {
        class Search(val apps: List<AppInfo>) : MenuItem()
        object Refresh : MenuItem()
        class ShowSystem(val checked: Boolean) : MenuItem()
        object Settings : MenuItem()
    }

    interface View {

        fun showAppInfoList(apps: List<AppInfo>, resetPosition: Boolean = false)

        fun showNoAppInfo()

        fun setLoadingIndicator(active: Boolean)

        fun setSortBy(sortBy: SortBy, order: Order)

        fun showSortByChooserDialog(sortBy: SortBy)

        fun setShowSystem(showSystem: Boolean)
    }

    interface Navigator {

        fun navigateSearch(sourceApps: List<AppInfo>)

        fun navigateSettings()
    }

    interface Presenter {

        @UiThread
        fun onCreate(shouldLoad: Boolean)

        @UiThread
        fun onResume()

        @UiThread
        fun onPause()

        @UiThread
        fun onDestroy()

        @UiThread
        fun onOptionsItemClicked(menuItem: MenuItem)

        @UiThread
        fun onPrepareShowSystemMenu()

        @UiThread
        fun onSortByClicked()

        @UiThread
        fun onSortByChoose(sortBy: SortBy, order: Order)
    }

    interface LoadAppInfoTask {

        fun isRunning(): Observable<Boolean>

        fun requestLoad(forceReload: Boolean = false): Observable<List<AppInfo>>
    }

    interface ShowSettingsTask {

        fun loadSortBy(): Single<Pair<SortBy, Order>>

        fun storeSortBy(sortBy: SortBy, order: Order): Single<Boolean>

        fun loadShowSystem(): Single<Boolean>

        fun storeShowSystem(showSystem: Boolean): Completable
    }

    class SortByChooseEvent(val sortBy: SortBy, val order: Order)
}