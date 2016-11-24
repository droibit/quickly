package com.droibit.quickly.main.search

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import rx.subscriptions.CompositeSubscription

fun searchModule(view: SearchContract.View, sourceApps: List<AppInfo>) = Kodein.Module {

    bind<SearchContract.View>() with instance(view)

    bind<SearchContract.Presenter>() with provider {
        SearchPresenter(
                view = instance(),
                showSettingsTask = instance(),
                sourceApps = sourceApps
        )
    }

    bind<SearchContract.ShowSettingsTask>() with provider {
        ShowSettingsTask(showSettingsRepository = instance())
    }

    bind<CompositeSubscription>() with singleton { CompositeSubscription() }
}
