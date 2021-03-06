package com.droibit.quickly.main.apps

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
import com.droibit.quickly.data.provider.intent.IntentCreator
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoContract
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.AppInfoAdapter
import com.droibit.quickly.main.MainContract.QuickActionEvent
import com.droibit.quickly.main.QuickActionDialogFragment
import com.droibit.quickly.main.apps.AppsContract.MenuItem
import com.droibit.quickly.main.apps.AppsContract.SortByChooseEvent
import com.droibit.quickly.main.search.SearchActivity
import com.droibit.quickly.settings.SettingsActivity
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber


class AppsActivity : AppCompatActivity(),
        AppsContract.View,
        AppsContract.Navigator,
        KodeinAware {

    companion object {

        private val TAG_FRAGMENT_SORT_BY_CHOOSER = "TAG_FRAGMENT_SORT_BY_CHOOSER"

        private val TAG_FRAGMENT_QUICK_ACTION = "TAG_FRAGMENT_QUICK_ACTION"

        private val REQUEST_UNINSTALL_APP = 1
    }

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val contentView: View by bindView(R.id.content)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val subTitleToolbar: SubtitleToolbar by bindView(R.id.subtitle)

    private val fab: FloatingActionButton by bindView(R.id.fab)

    private val injector = KodeinInjector()

    private val presenter: AppsContract.Presenter by injector.instance()

    private val appInfoComparators: AppInfoComparators by injector.instance()

    private val intentCreator: IntentCreator by injector.instance()

    private val localEventBus: RxBus by injector.instance("localEventBus")

    private val appEventBus: RxBus by injector.instance("appEventBus")

    private val subscriptions: CompositeSubscription by injector.instance()

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    private lateinit var showSystemMenuItem: android.view.MenuItem

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())

        val self = this@AppsActivity
        import(appsModule(view = self, navigator = self))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        setSupportActionBar(toolbar)

        injector.inject(kodein)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AppsActivity)
            adapter = appInfoAdapter
        }
        presenter.onCreate(shouldLoad = false)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        appInfoAdapter.apply {
            moreItemClickListener = {
                presenter.onMoreItemClicked(app = it)
            }
        }

        subTitleToolbar.sortByClickListener {
            presenter.onSortByClicked()
        }
        fab.setOnClickListener {
            presenter.onSearchButtonClicked()
        }
    }

    override fun onResume() {
        super.onResume()
        subscribeSortBy()
        subscribeQuickAction()
        subscribePackageAction()
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
        menuInflater.inflate(R.menu.apps, menu)
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

    // AppsContract.Navigator

    override fun navigateSearch() {
        val intent = SearchActivity.createIntent(this)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        ActivityCompat.startActivity(this, intent, options)
    }

    override fun navigateSettings() {
        startActivity(SettingsActivity.createIntent(this))
    }

    override fun navigateAppInfoInSettings(packageName: String) {
        val intent = intentCreator.newAppInfoIntent(packageName)
        startActivity(intent)
    }

    // AppsContract.View

    override fun showApps(apps: List<AppInfo>, resetPosition: Boolean) {
        Timber.d("showApps(apps=${apps.size})")
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = Math.max(0, layoutManager.findFirstVisibleItemPosition())
        val offsetPx = if (firstVisibleItemPosition > 0) {
            layoutManager.findViewByPosition(firstVisibleItemPosition).top
        } else {
            LinearLayoutManager.INVALID_OFFSET
        }
        Timber.d("firstVisibleItemPosition: $firstVisibleItemPosition, offsetPx: $offsetPx")

        if (appInfoAdapter.isEmpty) {
            appInfoAdapter.addAll(apps)
        } else {
            appInfoAdapter.replaceAll(apps, force = true)
        }

        layoutManager.scrollToPositionWithOffset(
                if (resetPosition) 0 else firstVisibleItemPosition,
                offsetPx
        )
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
        Timber.d("setSortBy($sortBy, $order")
        appInfoAdapter.apply {
            comparator = appInfoComparators.get(sortBy, order)
            refresh()
        }
        recyclerView.scrollToPosition(0)
        subTitleToolbar.sortBy(sortBy, order)
    }

    override fun showSortByChooserDialog(sortBy: SortBy) {
        Timber.d("showSortByChooserDialog($sortBy)")
        if (supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_SORT_BY_CHOOSER) == null) {
            val df = SortByChooserDialogFragment.newInstance(sortBy)
            df.show(supportFragmentManager, TAG_FRAGMENT_SORT_BY_CHOOSER)
        }
    }

    override fun showQuickActionSheet(app: AppInfo) {
        if (supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_QUICK_ACTION) == null) {
            QuickActionDialogFragment.newInstance(app)
                    .show(supportFragmentManager, TAG_FRAGMENT_QUICK_ACTION)
        }
    }

    override fun setShowSystem(showSystem: Boolean) {
        Timber.d("setShowSystem(showSystem=$showSystem)")
        showSystemMenuItem.isChecked = showSystem
    }

    override fun performUninstall(packageName: String) {
        val intent = intentCreator.newUninstallIntent(packageName)
        startActivityForResult(intent, REQUEST_UNINSTALL_APP)
    }

    override fun performSharePackage(packageName: String) {
        val intent = intentCreator.newShareIntent(text = packageName)
        startActivity(intent)
    }

    private fun subscribeSortBy() {
        localEventBus.asObservable()
                .ofType(SortByChooseEvent::class.java)
                .subscribe { presenter.onSortByChoose(it.sortBy, it.order) }
                .addTo(subscriptions)
    }

    private fun subscribeQuickAction() {
        localEventBus.asObservable()
                .ofType(QuickActionEvent::class.java)
                .subscribe { presenter.onQuickActionSelected(event = it) }
                .addTo(subscriptions)
    }

    private fun subscribePackageAction() {
        appEventBus.asObservable()
                .ofType(AppInfoContract.OnChangedEvent::class.java)
                .subscribe { presenter.onPackageChanged() }
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


