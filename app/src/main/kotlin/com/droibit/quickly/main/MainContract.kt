package com.droibit.quickly.main

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import rx.Completable
import rx.Observable
import rx.Single

interface MainContract {

    interface LoadAppInfoTask {

        fun isRunning(): Observable<Boolean>

        fun requestLoad(forceReload: Boolean = false): Observable<List<AppInfo>>
    }

    interface ShowSettingsTask {

        fun loadSortBy(): Single<Pair<ShowSettingsRepository.SortBy, ShowSettingsRepository.Order>>

        fun storeSortBy(sortBy: ShowSettingsRepository.SortBy, order: ShowSettingsRepository.Order): Single<Boolean>

        fun loadShowSystem(): Single<Boolean>

        fun storeShowSystem(showSystem: Boolean): Completable
    }

    enum class QuickActionItem(@DrawableRes val iconRes: Int, @StringRes val labelRes: Int) {
        UNINSTALL(iconRes = R.drawable.ic_uninstall, labelRes = R.string.quick_action_uninstall),
        SHARE_PACKAGE(iconRes = R.drawable.ic_share, labelRes = R.string.quick_action_share_package),
        OPEN_APP_INFO(iconRes = R.drawable.ic_settings, labelRes = R.string.quick_action_open_app_info);
    }

    sealed class QuickActionEvent(val app: AppInfo) {
        class Uninstall(app: AppInfo) : QuickActionEvent(app)
        class SharePackage(app: AppInfo) : QuickActionEvent(app)
        class OpenAppInfo(app: AppInfo) : QuickActionEvent(app)
    }
}