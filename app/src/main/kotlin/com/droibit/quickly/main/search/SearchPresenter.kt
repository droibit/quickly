package com.droibit.quickly.main.search

import android.support.annotation.UiThread
import com.droibit.quickly.data.repository.appinfo.AppInfo

class SearchPresenter (
        private val view: SearchContract.View,
        private val showSettingsTask: SearchContract.ShowSettingsTask): SearchContract.Presenter {

    @UiThread
    override fun onCreate(sourceApps: List<AppInfo>) {
        TODO()
    }
}