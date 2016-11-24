package com.droibit.quickly.main.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.droibit.quickly.R
import com.droibit.quickly.data.provider.comparators.AppInfoComparators
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.AppInfoAdapter
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.lapism.searchview.SearchView
import rx.Emitter
import rx.Observable
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*

class SearchActivity : AppCompatActivity(), SearchContract.View {

    companion object {

        @JvmStatic
        fun createIntent(context: Context, sourceApps: List<AppInfo>): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra(EXTRA_SOURCE_APPS, ArrayList(sourceApps))
            }
        }

        private val EXTRA_SOURCE_APPS = "EXTRA_SOURCE_APPS"
    }

    private val searchView: SearchView by bindView(R.id.search_app)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val injector = KodeinInjector()

    private val presenter: SearchContract.Presenter by injector.instance()

    private val appInfoComparators: AppInfoComparators by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        injector.inject(Kodein {
            extend(appKodein())

            val apps = savedInstanceState?.getParcelableArrayList<AppInfo>(EXTRA_SOURCE_APPS)
                    ?: intent.getParcelableArrayListExtra(EXTRA_SOURCE_APPS)
            val self = this@SearchActivity
            import(searchModule(view = self, sourceApps = apps))
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = appInfoAdapter
        }

        searchView.apply {
            setNavigationIcon(R.drawable.ic_search)
            open(true)
            showKeyboard()
        }
        presenter.onCreate()
    }

    override fun onResume() {
        super.onResume()
        subscribeSearchQueryText()
    }

    override fun onPause() {
        subscriptions.clear()
        super.onPause()
    }

    override fun onDestroy() {
        subscriptions.unsubscribe()
        super.onDestroy()
    }

    // SearchContract.View

    override fun showApps(apps: List<AppInfo>) {
        Timber.d("showAppInfoList(apps=${apps.size})")

        if (apps.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            appInfoAdapter.clear()
            return
        }

        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        appInfoAdapter.replaceAll(apps)
        recyclerView.scrollToPosition(0)
    }

    override fun setSortBy(sortBy: SortBy, order: Order) {
        appInfoAdapter.comparator = appInfoComparators.get(sortBy, order)
    }

    override fun closeSearch() {
        searchView.close(true)
    }

    // Private

    fun subscribeSearchQueryText() {
        searchView.queryText()
                .subscribe { presenter.onQueryTextEventEmitted(event = it) }
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
