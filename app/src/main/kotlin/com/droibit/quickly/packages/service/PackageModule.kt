package com.droibit.quickly.packages.service

import com.droibit.quickly.packages.PackageContract
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun packageModule() = Kodein.Module {

    bind<PackageContract.ActionPerformer>() with provider {
        PackageActionPerformer(
                appInfoRepository = instance(),
                appEventBus = instance("appEventBus")
        )
    }
}
