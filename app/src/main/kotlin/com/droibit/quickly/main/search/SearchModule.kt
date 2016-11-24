package com.droibit.quickly.main.search

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun searchModule(view: SearchContract.View) = Kodein.Module {

    bind<SearchContract.View>() with instance(view)

    bind<SearchContract.Presenter>() with provider {
        SearchPresenter(view = instance(), showSettingsTask = instance())
    }

    bind<SearchContract.ShowSettingsTask>() with provider {
        ShowSettingsTask(showSettingsRepository = instance())
    }
}
