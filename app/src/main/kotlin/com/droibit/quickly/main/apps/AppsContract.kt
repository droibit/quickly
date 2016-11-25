package com.droibit.quickly.main.apps

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy

interface AppsContract {

    sealed class MenuItem {
        object Refresh : MenuItem()
        class ShowSystem(val checked: Boolean) : MenuItem()
        object Settings : MenuItem()
    }

    interface View {

        fun showApps(apps: List<AppInfo>, resetPosition: Boolean = false)

        fun showNoAppInfo()

        fun setLoadingIndicator(active: Boolean)

        fun setSortBy(sortBy: SortBy, order: Order)

        fun showSortByChooserDialog(sortBy: SortBy)

        fun setShowSystem(showSystem: Boolean)
    }

    interface Navigator {

        fun navigateSearch()

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
        fun onSearchButtonClicked()

        @UiThread
        fun onSortByChoose(sortBy: SortBy, order: Order)
    }

    class SortByChooseEvent(val sortBy: SortBy, val order: Order)
}