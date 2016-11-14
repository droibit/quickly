package com.droibit.quickly.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.github.droibit.chopstick.bindView
import com.github.droibit.chopstick.findView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein

class MainActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val emptyView: View by bindView(R.id.empty)

    private val injector = KodeinInjector()

    private lateinit var appInfoAdapter: AppInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        injector.inject(Kodein {
            extend(appKodein())
        })

        appInfoAdapter = AppInfoAdapter(this).apply {
            addAll(
                    AppInfo(
                            packageName = "com.droibit.quickly",
                            name = "Qickly",
                            versionName = "1",
                            versionCode = 2,
                            icon = R.mipmap.ic_launcher,
                            preInstalled = false,
                            lastUpdateTime = System.currentTimeMillis()
                    ),
                    AppInfo(
                            packageName = "com.droibit.quickly.1",
                            name = "Qickly1",
                            versionName = "1",
                            versionCode = 2,
                            icon = R.mipmap.ic_launcher,
                            preInstalled = false,
                            lastUpdateTime = System.currentTimeMillis()
                    ),
                    AppInfo(
                            packageName = "com.droibit.quickly.2",
                            name = "Qickly2",
                            versionName = "1",
                            versionCode = 2,
                            icon = R.mipmap.ic_launcher,
                            preInstalled = false,
                            lastUpdateTime = System.currentTimeMillis()
                    ),
                    AppInfo(
                            packageName = "com.droibit.quickly.3",
                            name = "Qickly3",
                            versionName = "1",
                            versionCode = 2,
                            icon = R.mipmap.ic_launcher,
                            preInstalled = false,
                            lastUpdateTime = System.currentTimeMillis()
                    )
            )
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appInfoAdapter
            setHasFixedSize(true)
        }

        findView<TextView>(R.id.app_count).apply { text = getString(R.string.main_subtitle_app_count_format, 99) }
        findView<TextView>(R.id.sort_by_label).apply { text = getString(R.string.sorted_by_name) }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
