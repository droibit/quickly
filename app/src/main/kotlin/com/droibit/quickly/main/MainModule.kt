package com.droibit.quickly.main

import com.droibit.quickly.data.provider.eventbus.RxBus
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import com.jakewharton.rxrelay.BehaviorRelay
import rx.subscriptions.CompositeSubscription

fun mainModule(view: MainContract.View) = Kodein.Module {

    bind<MainContract.View>() with instance(view)

    bind<MainContract.Presenter>() with provider {
        MainPresenter(
                view = instance(),
                loadTask = instance(),
                sortByTask = instance(),
                subscriptions = instance()
        )
    }

    bind<MainContract.SortByTask>() with provider { SortByTask(showSettingsRepository = instance()) }

    bind<MainContract.LoadAppInfoTask>() with provider {
        LoadAppInfoTask(
                appInfoRepository = instance(),
                showSettingsRepository = instance(),
                runningRelay = instance()
        )
    }

    bind<CompositeSubscription>() with singleton { CompositeSubscription() }

    bind<BehaviorRelay<Boolean>>() with provider { BehaviorRelay.create<Boolean>() }

    bind<RxBus>() with singleton { RxBus() }
}