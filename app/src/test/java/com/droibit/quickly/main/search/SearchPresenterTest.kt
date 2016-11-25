package com.droibit.quickly.main.search

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.lang.kotlin.singleOf

class SearchPresenterTest {

    companion object {

        private val SOURCE_APPS = listOf(
                createAppInfo(packageName = "com.droibit.quickly", name = "Quickly"),
                createAppInfo(packageName = "com.droibit.news", name = "Daily"),
                createAppInfo(packageName = "com.droibit.books", name = "Magazine")
        )
    }

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Mock
    private lateinit var view: SearchContract.View

    @Mock
    private lateinit var showSettingsTask: MainContract.ShowSettingsTask

    private lateinit var presenter: SearchPresenter

    @Before
    fun setUp() {
        presenter = SearchPresenter(view, showSettingsTask, SOURCE_APPS)
    }

    @Test
    fun onCreate_setSortBy() {
        `when`(showSettingsTask.loadSortBy())
                .thenReturn(singleOf(Pair(SortBy.LAST_UPDATED, Order.DESC)))

        presenter.onCreate()
        verify(view).setSortBy(SortBy.LAST_UPDATED, Order.DESC)
    }

    @Test
    fun onQueryTextEventEmitted_change_showApps() {
        // hit both
        run {
            val event = QueryTextEvent.Change(query = "Quickly")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showApps(listOf(SOURCE_APPS[0]))
        }

        // hit app name
        run {
            val event = QueryTextEvent.Change(query = "daily")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showApps(listOf(SOURCE_APPS[1]))
        }
        reset(view)

        // hit package name
        run {
            val event = QueryTextEvent.Change(query = "books")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showApps(listOf(SOURCE_APPS[2]))
        }
    }

    @Test
    fun onQueryTextEventEmitted_submit_showApps() {
        val event = QueryTextEvent.Submit(query = "quickly")
        presenter.onQueryTextEventEmitted(event)

        verify(view).closeSearch()
    }
}

private fun createAppInfo(packageName: String, name: String): AppInfo {
    return AppInfo(
            packageName = packageName,
            name = name,
            versionName = "",
            versionCode = 0,
            icon = -1,
            preInstalled = false,
            lastUpdateTime = -1L
    )
}