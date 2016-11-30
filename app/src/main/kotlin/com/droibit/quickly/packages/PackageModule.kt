package com.droibit.quickly.packages

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun packageModule() = Kodein.Module {

    bind<PackageContract.ActionHandler>() with provider {
        PackageActionHandler(
                appInfoRepository = instance(),
                appEventBus = instance("appEventBus")
        )
    }
}