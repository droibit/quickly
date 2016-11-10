package com.droibit.quickly.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val emptyView: View by bindView(R.id.empty)

    private val injector = KodeinInjector()

    private val picasso: Picasso by injector.instance()

    private lateinit var appInfoAdapter: AppInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        injector.inject(Kodein {
            extend(appKodein())
        })

        appInfoAdapter = AppInfoAdapter(this, picasso).apply {
            addAll(listOf(
                    AppInfo(
                            packageName = "com.droibit.quickly",
                            name = "Qickly",
                            versionName = "1",
                            versionCode = 2,
                            icon = R.mipmap.ic_launcher,
                            preInstalled = false,
                            lastUpdateTime = System.currentTimeMillis()
                    )
            ))
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appInfoAdapter
            setHasFixedSize(true)
        }
    }
}
