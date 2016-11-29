package com.droibit.quickly

import android.app.Application
import com.droibit.quickly.data.config.provideConfigModule
import com.droibit.quickly.data.provider.providerModule
import com.droibit.quickly.data.repository.repositoryModule
import com.droibit.quickly.utils.LeakCanary
import com.droibit.quickly.utils.Stetho
import com.github.salomonbrys.kodein.*
import timber.log.Timber


class QuicklyApplication : Application(), KodeinAware {

    override val kodein: Kodein by Kodein.lazy {
        val context = this@QuicklyApplication
        import(applicationModule(context, debuggable = BuildConfig.DEBUG))
        import(repositoryModule())
        import(providerModule())
        import(provideConfigModule())
    }

    private val injector = KodeinInjector()

    private val timberTree: Timber.Tree by injector.instance()

    private val leakCanary: LeakCanary by injector.instance()

    private val stetho: Stetho by injector.instance()

    override fun onCreate() {
        super.onCreate()

        injector.inject(Kodein { extend(kodein) })

        Timber.plant(timberTree)

        leakCanary.initialize(this)
        stetho.initialize(this)
    }
}