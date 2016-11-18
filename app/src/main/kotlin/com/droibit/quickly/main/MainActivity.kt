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
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), MainContract.View {

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val contentView: View by bindView(R.id.content)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val subtitleToolbar: SubtitleToolbar by bindView(R.id.subtitle)

    private val injector = KodeinInjector()

    private val presenter: MainContract.Presenter by injector.instance()

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        injector.inject(Kodein {
            extend(appKodein())
            import(mainModule(view = this@MainActivity))
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appInfoAdapter
        }

        presenter.onCreate(shouldLoad = false)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.onOptionsItemClicked(MainContract.MenuItem.from(item.itemId))
        return true
    }

    // MainContract.View

    override fun showAppInfoList(appInfos: List<AppInfo>) {
        Timber.d("showAppInfoList(appInfos=${appInfos.size})")
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        appInfoAdapter.addAll(appInfos)
        subtitleToolbar.appCount = appInfos.size
    }

    override fun showNoAppInfo() {
        TODO()
    }

    override fun setLoadingIndicator(active: Boolean) {
        Timber.d("setLoadingIndicator(active=%b)", active)
        progressBar.visibility = if (active) View.VISIBLE else View.GONE
        contentView.visibility = if (active) View.GONE else View.VISIBLE
    }

    override fun setComparator(comparator: Comparator<AppInfo>) {
        appInfoAdapter.comparator = comparator
    }
}
