package com.droibit.quickly.main

import android.support.annotation.IdRes
import android.support.annotation.UiThread
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Completable
import rx.Observable
import rx.Single

interface MainContract {

    enum class MenuItem(@IdRes val id: Int) {
        SEARCH(R.id.search),
        REFRESH(R.id.refresh),
        SHOW_SYSTEM(R.id.show_system),
        SETTINGS(R.id.settings);

        companion object {
            @JvmStatic
            fun from(@IdRes id: Int) = values().first { it.id == id }
        }
    }

    interface View {

        fun showAppInfoList(apps: List<AppInfo>)

        fun showNoAppInfo()

        fun setLoadingIndicator(active: Boolean)

        fun setSortBy(sortBy: SortBy, order: Order)

        fun showSortByChooserDialog(sortBy: SortBy)

        fun setShowSystem(showSystem: Boolean)
    }

    interface Navigator {

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