package com.droibit.quickly.main

import android.support.annotation.IdRes
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Observable

interface MainContract {

    enum class MenuItem(@IdRes val id: Int) {
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

        fun onCreate(shouldLoad: Boolean)

        fun onResume()

        fun onPause()

        fun onOptionsItemClicked(menuItem: MenuItem)

        fun onSortByClicked()
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

    class SortByChooseEvevent(sortBy: SortBy, order: Order)
}