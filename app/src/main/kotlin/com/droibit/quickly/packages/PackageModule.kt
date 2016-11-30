package com.droibit.quickly.packages

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun packageModule(context: Context, receiver: PackageContract.Receiver) = Kodein.Module {

    bind<Context>() with instance(context)

    bind<PackageContract.Receiver>(overrides = true) with instance(receiver)

    bind<PackageContract.ActionHandler>() with provider {
        PackageActionHandler(receiver = instance())
    }
}