package com.droibit.quickly.main.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.AppInfoAdapter
import com.github.droibit.chopstick.bindView
import com.lapism.searchview.SearchView
import java.util.*
import kotlin.comparisons.compareBy

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

    private val appInfoAdapter: AppInfoAdapter by lazy { AppInfoAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        appInfoAdapter.apply {
            comparator = compareBy(AppInfo::name)

            val apps = intent.getParcelableArrayListExtra<AppInfo>(EXTRA_SOURCE_APPS)
            addAll(apps)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = appInfoAdapter
        }

        searchView.setNavigationIcon(R.drawable.ic_search)
        searchView.open(true)
    }

    // SearchContract.View

    override fun showApps(apps: List<AppInfo>, sortBy: SortBy, order: Order) {
        TODO()
    }
}
