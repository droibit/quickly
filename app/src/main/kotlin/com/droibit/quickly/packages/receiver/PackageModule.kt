package com.droibit.quickly.packages.receiver

import android.content.Context
import com.droibit.quickly.packages.PackageContract
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun packageModule(context: Context, receiver: PackageContract.Receiver) = Kodein.Module {

    bind<Context>() with instance(context)

    bind<PackageContract.Receiver>() with instance(receiver)

    bind<PackageContract.ActionHandler>() with provider {
        PackageActionHandler(receiver = instance())
    }
}