package com.droibit.quickly.main.search

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.main.mainModule
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import rx.subscriptions.CompositeSubscription
import java.util.*

fun searchModule(
        view: SearchContract.View,
        navigator: SearchContract.Navigator) = Kodein.Module {
    import(mainModule())

    bind<SearchContract.View>() with instance(view)

    bind<SearchContract.Navigator>() with instance(navigator)

    bind<SearchContract.Presenter>() with provider {
        SearchPresenter(
                view = instance(),
                navigator = instance(),
                loadTask = instance(),
                showSettingsTask = instance(),
                subscriptions = instance(),
                sourceApps = instance()
        )
    }

    bind<CompositeSubscription>() with singleton { CompositeSubscription() }

    bind<MutableList<AppInfo>>() with provider { ArrayList<AppInfo>() }
}
