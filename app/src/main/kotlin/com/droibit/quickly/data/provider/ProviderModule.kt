package com.droibit.quickly.data.provider

import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.appinfo.AppInfoRepositoryImpl
import com.droibit.quickly.data.repository.source.AppInfoDataSource
import com.droibit.quickly.data.repository.source.AppInfoDataSourceImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

fun providerModule() = Kodein.Module {

    bind<AppInfoDataSource>() with singleton { AppInfoDataSourceImpl(pm = instance()) }

    bind<AppInfoRepository>() with singleton {
        AppInfoRepositoryImpl(
                orma = instance(),
                appInfoSource = instance()
        )
    }
}