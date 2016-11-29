package com.droibit.quickly

import android.content.Context
import android.content.pm.PackageManager
import com.droibit.quickly.data.repository.appinfo.OrmaDatabase
import com.droibit.quickly.utils.LeakCanary
import com.droibit.quickly.utils.Stetho
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import timber.log.Timber

fun applicationModule(context: Context, debuggable: Boolean) = Kodein.Module {

    bind<Context>() with instance(context)

    bind<PackageManager>() with singleton { context.packageManager }

    bind<OrmaDatabase>() with singleton {
        OrmaDatabase.Builder(instance())
                .name(null)
                .build()
    }

    bind<Timber.Tree>() with singleton { if (debuggable) Timber.DebugTree() else EmptyTimberTree() }

    bind<LeakCanary>() with singleton { LeakCanary }

    bind<Stetho>() with singleton { Stetho }
}

private class EmptyTimberTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
    }
}
