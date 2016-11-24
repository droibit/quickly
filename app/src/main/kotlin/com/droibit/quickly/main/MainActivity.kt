package com.droibit.quickly.main

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import com.droibit.quickly.R
import com.droibit.quickly.data.provider.comparators.AppInfoComparators
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.SortByChooseEvent
import com.droibit.quickly.main.MainContract.MenuItem
import com.droibit.quickly.main.search.SearchActivity
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber


class MainActivity : AppCompatActivity(),
        MainContract.View,
        MainContract.Navigator,
        KodeinAware {

    companion object {

        private val TAG_FRAGMENT_SORT_BY_CHOOSER = "TAG_FRAGMENT_SORT_BY_CHOOSER"
    }

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val contentView: View by bindView(R.id.content)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val subTitleToolbar: SubtitleToolbar by bindView(R.id.subtitle)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private val injector = KodeinInjector()

    private val presenter: MainContract.Presenter by injector.instance()

    private val appInfoComparators: AppInfoComparators by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    private lateinit var showSystemMenuItem: android.view.MenuItem

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())

        val self = this@MainActivity
        import(mainModule(view = self, navigator = self))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        injector.inject(kodein)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appInfoAdapter
        }

        subTitleToolbar.sortByClickListener {
            presenter.onSortByClicked()
        }
        fab.setOnClickListener {
            presenter.onSearchButtonClicked()
        }
        presenter.onCreate(shouldLoad = false)
    }

    override fun onResume() {
        super.onResume()
        subscribeSortBy()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        showSystemMenuItem = menu.findItem(R.id.show_system)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        presenter.onPrepareShowSystemMenu()
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        presenter.onOptionsItemClicked(item.toAppMenuItem())
        return true
    }

    // MainContract.Navigator
    override fun navigateSearch() {
        val intent = SearchActivity.createIntent(this, sourceApps = appInfoAdapter.items)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        ActivityCompat.startActivity(this, intent, options)
    }

    override fun navigateSettings() {
        TODO()
    }

    // MainContract.View

    override fun showAppInfoList(apps: List<AppInfo>, resetPosition: Boolean) {
        Timber.d("showAppInfoList(apps=${apps.size})")
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        if (appInfoAdapter.isEmpty) {
            appInfoAdapter.addAll(apps)
        } else {
            appInfoAdapter.replaceAll(apps)
        }

        if (resetPosition) {
            recyclerView.scrollToPosition(0)
        }
        subTitleToolbar.appCount = apps.size
    }

    override fun showNoAppInfo() {
        TODO()
    }

    override fun setLoadingIndicator(active: Boolean) {
        Timber.d("setLoadingIndicator(active=%b)", active)
        progressBar.visibility = if (active) View.VISIBLE else View.GONE
        contentView.visibility = if (active) View.GONE else View.VISIBLE
    }

    override fun setSortBy(sortBy: SortBy, order: Order) {
        appInfoAdapter.apply {
            comparator = appInfoComparators.get(sortBy, order)
            refresh()
        }
        recyclerView.scrollToPosition(0)
        subTitleToolbar.sortBy(sortBy, order)
    }

    override fun showSortByChooserDialog(sortBy: SortBy) {
        if (supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_SORT_BY_CHOOSER) == null) {
            val df = SortByChooserDialogFragment.newInstance(sortBy)
            df.show(supportFragmentManager, TAG_FRAGMENT_SORT_BY_CHOOSER)
        }
    }

    override fun setShowSystem(showSystem: Boolean) {
        showSystemMenuItem.isChecked = showSystem
    }

    private fun subscribeSortBy() {
        rxBus.asObservable()
                .ofType(SortByChooseEvent::class.java)
                .subscribe { presenter.onSortByChoose(it.sortBy, it.order) }
                .addTo(subscriptions)
    }

    private fun android.view.MenuItem.toAppMenuItem(): MenuItem {
        return when (itemId) {
            R.id.refresh -> MenuItem.Refresh
            R.id.show_system -> MenuItem.ShowSystem(!isChecked)
            R.id.settings -> MenuItem.Settings
            else -> throw IllegalStateException("unknown menuItem: $title")
        }
    }
}


