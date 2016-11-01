package com.droibit.quickly

import android.content.Context
import com.droibit.quickly.data.repository.appinfo.OrmaDatabase
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.squareup.picasso.Picasso
import timber.log.Timber

fun applicationModule(context: Context, debuggable: Boolean) = Kodein.Module {

    bind<Context>() with instance(context)

    bind<OrmaDatabase>() with singleton { OrmaDatabase.Builder(instance()).build() }

    bind<Timber.Tree>() with singleton { if (debuggable) Timber.DebugTree() else EmptyTimberTree() }

    bind<Picasso>() with singleton { Picasso.with(instance()) }
}

private class EmptyTimberTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
    }
}
