package com.droibit.quickly.main.apps

import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.main.LoadAppInfoTask
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.ShowSettingsTask
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import com.jakewharton.rxrelay.PublishRelay
import rx.subscriptions.CompositeSubscription

fun appsModule(view: AppsContract.View, navigator: AppsContract.Navigator) = Kodein.Module {

    bind<AppsContract.View>() with instance(view)

    bind<AppsContract.Navigator>() with instance(navigator)

    bind<AppsContract.Presenter>() with provider {
        AppsPresenter(
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