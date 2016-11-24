package com.droibit.quickly.main

import com.droibit.quickly.data.provider.eventbus.RxBus
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import com.jakewharton.rxrelay.PublishRelay
import rx.subscriptions.CompositeSubscription

fun mainModule(view: MainContract.View, navigator: MainContract.Navigator) = Kodein.Module {

    bind<MainContract.View>() with instance(view)

    bind<MainContract.Navigator>() with instance(navigator)

    bind<MainContract.Presenter>() with provider {
        MainPresenter(
                view = instance(),
                navigator = instance(),
                loadTask = instance(),
                showSettingsTask = instance(),
                subscriptions = instance()
        )
    }

    bind<MainContract.ShowSettingsTask>() with provider {
        ShowSettingsTask(showSettingsRepository = instance())
    }

    bind<MainContract.LoadAppInfoTask>() with provider {
        LoadAppInfoTask(
                appInfoRepository = instance(),
                showSettingsRepository = instance(),
                runningRelay = instance()
        )
    }

    bind<CompositeSubscription>() with singleton { CompositeSubscription() }

    bind<PublishRelay<Boolean>>() with provider { PublishRelay.create<Boolean>() }

    bind<RxBus>() with singleton { RxBus() }
}