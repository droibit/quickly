package com.droibit.quickly.main.search

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.search.SearchContract.QueryTextEvent
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.Observable
import rx.lang.kotlin.singleOf
import rx.lang.kotlin.toSingletonObservable
import rx.subscriptions.CompositeSubscription

@Suppress("UNCHECKED_CAST")
class SearchPresenterTest {

    companion object {

        private val SOURCE_APPS = mutableListOf(
                createAppInfo(packageName = "com.droibit.quickly", name = "Quickly"),
                createAppInfo(packageName = "com.droibit.news", name = "Daily"),
                createAppInfo(packageName = "com.droibit.books", name = "Magazine")
        )
    }

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    private lateinit var view: SearchContract.View

    @Mock
    private lateinit var navigator: SearchContract.Navigator

    @Mock
    private lateinit var loadTask: MainContract.LoadAppInfoTask

    @Mock
    private lateinit var showSettingsTask: MainContract.ShowSettingsTask

    @Mock
    private lateinit var sourceApps: MutableList<AppInfo>

    private lateinit var subscriptions: CompositeSubscription

    private lateinit var presenter: SearchPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = SearchPresenter(
                view,
                navigator,
                loadTask,
                showSettingsTask,
                subscriptions,
                sourceApps
        )
    }

    @Test
    fun onCreate_setSortBy() {
        `when`(showSettingsTask.loadSortBy())
                .thenReturn(singleOf(Pair(SortBy.LAST_UPDATED, Order.DESC)))

        presenter.onCreate()
        verify(view).setSortBy(SortBy.LAST_UPDATED, Order.DESC)
    }

    @Test
    fun onResume_subscribeRunning() {
        `when`(loadTask.requestLoad()).thenReturn(Observable.empty())

        `when`(loadTask.isRunning()).thenReturn(true.toSingletonObservable())
        presenter.onResume()
        verify(view).setLoadingIndicator(true)

        `when`(loadTask.isRunning()).thenReturn(false.toSingletonObservable())
        presenter.onResume()
        verify(view).setLoadingIndicator(false)
    }

    @Test
    fun onResume_subscribeApps() {
        `when`(loadTask.isRunning()).thenReturn(Observable.empty())
        `when`(view.searchQuery).thenReturn("")

        val mockList = mock(List::class.java) as List<AppInfo>
        `when`(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())
        `when`(sourceApps.isEmpty()).thenReturn(true)

        presenter.onResume()
        verify(sourceApps).addAll(mockList)
        verify(view).showNoApps()
    }

    @Test
    fun onQueryTextEventEmitted_showAppsOnChanged() {
        // hit both
        run {
            `when`(sourceApps.iterator()).thenReturn(SOURCE_APPS.iterator())
            val event = QueryTextEvent.Change(query = "Quickly")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showApps(listOf(SOURCE_APPS[0]), resetPosition = true)
        }
        reset(view)

        // hit app name
        run {
            `when`(sourceApps.iterator()).thenReturn(SOURCE_APPS.iterator())
            val event = QueryTextEvent.Change(query = "daily")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showApps(listOf(SOURCE_APPS[1]), resetPosition = true)
        }
        reset(view)

        // hit package name
        run {
            `when`(sourceApps.iterator()).thenReturn(SOURCE_APPS.iterator())
            val event = QueryTextEvent.Change(query = "books")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showApps(listOf(SOURCE_APPS[2]), resetPosition = true)
        }
    }

    @Test
    fun onQueryTextEventEmitted_showNoApps() {
        // empty query
        run {
            `when`(sourceApps.iterator()).thenReturn(SOURCE_APPS.iterator())
            val event = QueryTextEvent.Change(query = "")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showNoApps()
        }
        reset(view)

        // no hit
        run {
            `when`(sourceApps.iterator()).thenReturn(SOURCE_APPS.iterator())
            val event = QueryTextEvent.Change(query = "no_hit")
            presenter.onQueryTextEventEmitted(event)

            verify(view).showNoApps()
        }
    }

    @Test
    fun onQueryTextEventEmitted_showAppsOnSubmit() {
        val event = QueryTextEvent.Submit(query = "quickly")
        presenter.onQueryTextEventEmitted(event)

        verify(view).closeSearch()
    }

    @Test
    fun onQuickActionSelected_performUninstall() {
        val app = SOURCE_APPS.first()
        presenter.onQuickActionSelected(MainContract.QuickActionEvent.Uninstall(app))

        verify(view).performUninstall(app.packageName)
        verify(view, never()).performSharePackage(anyString())
        verify(navigator, never()).navigateAppInfoInSettings(anyString())
    }

    @Test
    fun onQuickActionSelected_performSharePackage() {
        val app = SOURCE_APPS.first()
        presenter.onQuickActionSelected(MainContract.QuickActionEvent.SharePackage(app))

        verify(view).performSharePackage(app.packageName)
        verify(view, never()).performUninstall(Matchers.anyString())
        verify(navigator, never()).navigateAppInfoInSettings(anyString())
    }

    @Test
    fun onQuickActionSelected_navigateAppInfoInSettings() {
        val app = SOURCE_APPS.first()
        presenter.onQuickActionSelected(MainContract.QuickActionEvent.OpenAppInfo(app))

        verify(navigator).navigateAppInfoInSettings(anyString())
        verify(view, never()).performSharePackage(app.packageName)
        verify(view, never()).performUninstall(Matchers.anyString())
    }

    @Test
    fun onPackageChanged_showApps() {
        `when`(loadTask.isRunning()).thenReturn(Observable.empty())
        `when`(view.searchQuery).thenReturn("")

        val mockList = mock(List::class.java) as List<AppInfo>
        `when`(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())
        `when`(sourceApps.isEmpty()).thenReturn(true)

        presenter.onPackageChanged()
        verify(sourceApps).addAll(mockList)
        verify(view).showNoApps()
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