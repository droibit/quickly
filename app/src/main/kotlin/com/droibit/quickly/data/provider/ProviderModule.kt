package com.droibit.quickly.data.provider

import com.droibit.quickly.data.provider.comparators.AppInfoComparators
import com.droibit.quickly.data.provider.date.DateFormatter
import com.droibit.quickly.data.provider.eventbus.PublishBus
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.provider.intent.IntentCreator
import com.droibit.quickly.data.provider.intent.IntentCreatorImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun providerModule() = Kodein.Module {

    bind<DateFormatter>() with singleton { DateFormatter(context = instance()) }

    bind<AppInfoComparators>() with singleton { AppInfoComparators }

    bind<IntentCreator>() with singleton { IntentCreatorImpl }

    bind<RxBus>("appEventBus") with singleton { PublishBus() }
}