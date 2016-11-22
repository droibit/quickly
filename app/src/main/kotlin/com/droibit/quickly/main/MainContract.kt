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

        fun showAppInfoList(appInfos: List<AppInfo>)

        fun showNoAppInfo()

        fun setLoadingIndicator(active: Boolean)

        fun setSortBy(sortBy: SortBy, order: Order)

        fun showSortByChooserDialog(sortBy: SortBy)
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
        fun onSortByClicked()

        @UiThread
        fun onSortByChoose(sortBy: SortBy, order: Order)
    }

    interface LoadAppInfoTask {

        sealed class LoadEvent {
            class OnResult(val appInfos: List<AppInfo>) : LoadEvent()
            object Nothing : LoadEvent()
        }

        fun asObservable(): Observable<LoadEvent>

        fun isRunning(): Observable<Boolean>

        fun requestLoad(forceReload: Boolean = false)
    }

    interface SortByTask {

        fun load(): Single<Pair<SortBy, Order>>

        fun store(sortBy: SortBy, order: Order): Single<Boolean>
    }

    class SortByChooseEvent(val sortBy: SortBy, val order: Order)
}