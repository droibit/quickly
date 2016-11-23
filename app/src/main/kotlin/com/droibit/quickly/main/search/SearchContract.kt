package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import rx.Single

interface SearchContract {

    interface View {

        fun showApps(apps: List<AppInfo>, sortBy: SortBy, order: Order)
    }

    interface Presenter {

        @UiThread
        fun onCreate(sourceApps: List<AppInfo>)
    }

    interface ShowSettingsTask {

        fun load(): Single<Pair<SortBy, Order>>
    }
}