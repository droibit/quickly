package com.droibit.quickly.data.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton

fun provideConfigModule() = Kodein.Module {

    bind<ApplicationConfig>() with singleton { ApplicationConfigImpl() }
}
