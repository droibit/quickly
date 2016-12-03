package com.droibit.quickly.main.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.QuickActionDialogFragment
import com.droibit.quickly.main.apps.AppsActivity
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import com.lapism.searchview.SearchView
import rx.Emitter
import rx.Observable
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*

class SearchActivity : AppCompatActivity(),
        SearchContract.View,
        SearchContract.Navigator,
        KodeinAware {

    companion object {

        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }

        private val TAG_FRAGMENT_QUICK_ACTION = "TAG_FRAGMENT_QUICK_ACTION"
    }

    private val searchView: SearchView by bindView(R.id.search_app)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val injector = KodeinInjector()

    private val presenter: SearchContract.Presenter by injector.instance()

    private val appInfoComparators: AppInfoComparators by injector.instance()

    private val localEventBus: RxBus by injector.instance("localEventBus")

    private val appEventBus: RxBus by injector.instance("appEventBus")

    private val intentCreator: IntentCreator by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    override val searchQuery: String
        get() = searchView.query.toString()

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())

        val self = this@SearchActivity
        import(searchModule(view = self, navigator = self))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        injector.inject(kodein)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = appInfoAdapter
        }
        searchView.setNavigationIcon(R.drawable.ic_search)

        presenter.onCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        appInfoAdapter.apply {
            moreItemClickListener = {
                presenter.onMoreItemClicked(app = it)
            }
        }

        searchView.apply {
            open(true)
            showKeyboard()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        subscribeSearchQueryText()
        subscribeQuickAction()
        subscribePackageAction()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // SearchContract.Navigator

    override fun navigateAppInfoInSettings(packageName: String) {
        val intent = intentCreator.newAppInfoIntent(packageName)
        startActivity(intent)
    }

    // SearchContract.View

    override fun showApps(apps: List<AppInfo>) {
        Timber.d("showApps(apps=${apps.size})")

        emptyView.visibility = View.GONE

        appInfoAdapter.replaceAll(apps)
        recyclerView.scrollToPosition(0)
    }

    override fun showNoApps() {
        Timber.d("showNoApps()")

        emptyView.visibility = View.VISIBLE
        appInfoAdapter.clear()
    }

    override fun setLoadingIndicator(active: Boolean) {
        Timber.d("setLoadingIndicator(active=%b)", active)
        progressBar.visibility = if (active) View.VISIBLE else View.GONE
        emptyView.visibility = if (active || !appInfoAdapter.isEmpty) View.GONE else View.VISIBLE
    }

    override fun setSortBy(sortBy: SortBy, order: Order) {
        appInfoAdapter.comparator = appInfoComparators.get(sortBy, order)
    }

    override fun closeSearch() {
        searchView.close(true)
    }

    override fun showQuickActionSheet(app: AppInfo) {
        if (supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_QUICK_ACTION) == null) {
            QuickActionDialogFragment.newInstance(app)
                    .show(supportFragmentManager, TAG_FRAGMENT_QUICK_ACTION)
        }
    }

    override fun performUninstall(packageName: String) {
        val intent = intentCreator.newUninstallIntent(packageName)
        startActivity(intent)
    }

    override fun performSharePackage(packageName: String) {
        val intent = intentCreator.newShareIntent(text = packageName)
        startActivity(intent)
    }

    // Private

    fun subscribeSearchQueryText() {
        searchView.queryText()
                .subscribe { presenter.onQueryTextEventEmitted(event = it) }
                .addTo(subscriptions)
    }

    private fun subscribeQuickAction() {
        localEventBus.asObservable()
                .ofType(MainContract.QuickActionEvent::class.java)
                .subscribe { presenter.onQuickActionSelected(event = it) }
                .addTo(subscriptions)
    }

    private fun subscribePackageAction() {
        appEventBus.asObservable()
                .ofType(AppInfoContract.OnChangedEvent::class.java)
                .subscribe { presenter.onPackageChanged() }
                .addTo(subscriptions)
    }
}

private fun SearchView.queryText(): Observable<QueryTextEvent> {
    return Observable.fromEmitter<QueryTextEvent>({ emitter ->
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                emitter.onNext(QueryTextEvent.Submit(query))
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                emitter.onNext(QueryTextEvent.Change(newText))
                return true
            }
        })

        emitter.setCancellation {
            setOnQueryTextListener(null)
        }
    }, Emitter.BackpressureMode.BUFFER)
}
