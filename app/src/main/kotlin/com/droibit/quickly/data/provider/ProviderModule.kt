package com.droibit.quickly.data.provider

import com.droibit.quickly.data.provider.date.DateFormatter
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun providerModule() = Kodein.Module {

    bind<DateFormatter>() with singleton { DateFormatter(context = instance()) }
}