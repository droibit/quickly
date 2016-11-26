package com.droibit.quickly.main.apps

import com.droibit.quickly.main.mainModule
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import rx.subscriptions.CompositeSubscription

fun appsModule(view: AppsContract.View, navigator: AppsContract.Navigator) = Kodein.Module {
    import(mainModule())

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

    bind<CompositeSubscription>() with singleton { CompositeSubscription() }
}