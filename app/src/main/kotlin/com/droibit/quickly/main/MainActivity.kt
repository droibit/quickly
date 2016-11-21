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
import com.droibit.quickly.data.provider.comparators.AppInfoComparators
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.SortByChooseEvent
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import rx.lang.kotlin.addTo
import rx.subscriptions.CompositeSubscription
import timber.log.Timber


class MainActivity : AppCompatActivity(), MainContract.View, KodeinAware {

    companion object {

        private val TAG_FRAGMENT_SORT_BY_CHOOSER = "TAG_FRAGMENT_SORT_BY_CHOOSER"
    }

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val emptyView: View by bindView(R.id.empty)

    private val contentView: View by bindView(R.id.content)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val subTitleToolbar: SubtitleToolbar by bindView(R.id.subtitle)

    private val injector = KodeinInjector()

    private val presenter: MainContract.Presenter by injector.instance()

    private val appInfoComparators: AppInfoComparators by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private val subscriptions: CompositeSubscription by injector.instance()

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    override val kodein: Kodein by Kodein.lazy {
        extend(appKodein())
        import(mainModule(view = this@MainActivity))
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

        presenter.onCreate(shouldLoad = false)
    }

    override fun onResume() {
        super.onResume()
        subscribeSortBy()
        presenter.onResume()
    }

    override fun onPause() {
        subscriptions.clear()
        presenter.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        subscriptions.unsubscribe()
        super.onDestroy()
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
        subTitleToolbar.appCount = appInfos.size
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
        appInfoAdapter.comparator = appInfoComparators.get(sortBy, order)
        subTitleToolbar.sortBy(sortBy, order)
    }

    override fun showSortByChooserDialog(sortBy: SortBy) {
        if (supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_SORT_BY_CHOOSER) == null) {
            val df = SortByChooserDialogFragment.newInstance(sortBy)
            df.show(supportFragmentManager, TAG_FRAGMENT_SORT_BY_CHOOSER)
        }
    }

    private fun subscribeSortBy() {
        rxBus.asObservable()
                .ofType(SortByChooseEvent::class.java)
                .subscribe { presenter.onSortByChoose(it.sortBy, it.order) }
                .addTo(subscriptions)
    }
}
