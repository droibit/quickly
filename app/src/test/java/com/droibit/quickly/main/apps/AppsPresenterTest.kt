package com.droibit.quickly.main.apps

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.MainContract.QuickActionEvent
import com.droibit.quickly.main.apps.AppsContract.MenuItem
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.Completable
import rx.Observable
import rx.lang.kotlin.singleOf
import rx.lang.kotlin.toSingletonObservable
import rx.subscriptions.CompositeSubscription

@Suppress("UNCHECKED_CAST")
class AppsPresenterTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    private lateinit var view: AppsContract.View

    @Mock
    private lateinit var navigator: AppsContract.Navigator

    @Mock
    private lateinit var loadTask: MainContract.LoadAppInfoTask

    @Mock
    private lateinit var showSettingsTask: MainContract.ShowSettingsTask

    private lateinit var subscriptions: CompositeSubscription

    private lateinit var presenter: AppsPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = AppsPresenter(
                view,
                navigator,
                loadTask,
                showSettingsTask,
                subscriptions
        )
    }

    @Test
    fun onCreate_setSortBy() {
        val result = Pair(SortBy.LAST_UPDATED, Order.DESC)
        whenever(showSettingsTask.loadSortBy()).thenReturn(singleOf(result))

        presenter.onCreate(true)
        verify(view).setSortBy(result.first, result.second)
    }

    @Test
    fun onResume_subscribeRunning() {
        whenever(loadTask.requestLoad()).thenReturn(Observable.empty())

        whenever(loadTask.isRunning()).thenReturn(true.toSingletonObservable())
        presenter.onResume()
        verify(view).setLoadingIndicator(true)

        whenever(loadTask.isRunning()).thenReturn(false.toSingletonObservable())
        presenter.onResume()
        verify(view).setLoadingIndicator(false)
    }

    @Test
    fun onResume_subscribeApps() {
        whenever(loadTask.isRunning()).thenReturn(Observable.empty())

        val mockList: List<AppInfo> = mock()
        whenever(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())

        run {
            whenever(mockList.isEmpty()).thenReturn(false)

            presenter.onResume()
            verify(view).showApps(mockList)
        }

        run {
            whenever(mockList.isEmpty()).thenReturn(true)

            presenter.onResume()
            verify(view).showNoAppInfo()
        }
    }

    @Test
    fun onPrepareShowSystemMenu_setShowSystem() {
        whenever(showSettingsTask.loadShowSystem()).thenReturn(singleOf(true))

        presenter.onPrepareShowSystemMenu()
        verify(view).setShowSystem(true)
    }

    @Test
    fun onOptionsItemClicked_showSystem_showApps() {
        whenever(showSettingsTask.storeShowSystem(any())).thenReturn(Completable.complete())

        val mockList: List<AppInfo> = mock() {
            on { isEmpty() } doReturn false
        }
        whenever(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())

        presenter.onOptionsItemClicked(MenuItem.ShowSystem(checked = true))

        verify(view).showApps(mockList, true)
    }

    @Test
    fun onOptionsItemClicked_refresh_showApps() {
        val mockList: List<AppInfo> = mock() {
            on { isEmpty() } doReturn false
        }
        whenever(loadTask.requestLoad(any())).thenReturn(mockList.toSingletonObservable())

        presenter.onOptionsItemClicked(MenuItem.Refresh)

        verify(view).showApps(mockList, true)
    }

    @Test
    fun onOptionsItemClicked_settings_navigateSettings() {
        presenter.onOptionsItemClicked(MenuItem.Settings)

        verify(navigator).navigateSettings()
    }

    @Test
    fun onMoreItemClicked_showQuickActionSheet() {
        val app = createAppInfo(id = 1)
        presenter.onMoreItemClicked(app)

        verify(view).showQuickActionSheet(app)
    }

    @Test
    fun onSortByClicked_showSortByChooserDialog() {
        val result = Pair(SortBy.NAME, Order.ASC)
        whenever(showSettingsTask.loadSortBy()).thenReturn(singleOf(result))

        presenter.onSortByClicked()
        verify(view).showSortByChooserDialog(sortBy = result.first)
    }

    @Test
    fun onSearchButtonClicked_navigateSearch() {
        presenter.onSearchButtonClicked()

        verify(navigator).navigateSearch()
    }

    @Test
    fun onSortByChoose_setSortBy() {
        val result = Pair(SortBy.LAST_UPDATED, Order.DESC)
        whenever(showSettingsTask.storeSortBy(result.first, result.second)).thenReturn(singleOf(true))

        presenter.onSortByChoose(result.first, result.second)
        verify(view).setSortBy(result.first, result.second)
    }

    @Test
    fun onQuickActionSelected_performUninstall() {
        val app = createAppInfo(id = 1)
        presenter.onQuickActionSelected(QuickActionEvent.Uninstall(app))

        verify(view).performUninstall(app.packageName)
        verify(view, never()).performSharePackage(any())
        verify(navigator, never()).navigateAppInfoInSettings(any())
    }

    @Test
    fun onQuickActionSelected_performSharePackage() {
        val app = createAppInfo(id = 2)
        presenter.onQuickActionSelected(QuickActionEvent.SharePackage(app))

        verify(view).performSharePackage(app.packageName)
        verify(view, never()).performUninstall(any())
        verify(navigator, never()).navigateAppInfoInSettings(any())
    }

    @Test
    fun onQuickActionSelected_navigateAppInfoInSettings() {
        val app = createAppInfo(id = 3)
        presenter.onQuickActionSelected(QuickActionEvent.OpenAppInfo(app))

        verify(navigator).navigateAppInfoInSettings(any())
        verify(view, never()).performSharePackage(app.packageName)
        verify(view, never()).performUninstall(any())
    }

    @Test
    fun onPackageChanged_showApps() {
        val mockList: List<AppInfo> = mock() {
            on { isEmpty() } doReturn false
        }
        whenever(loadTask.requestLoad(any())).thenReturn(mockList.toSingletonObservable())

        presenter.onPackageChanged()

        verify(view).showApps(mockList, false)
    }
}

private fun createAppInfo(id: Int): AppInfo {
    return AppInfo(
            packageName = "app.$id",
            name = "app-$id",
            versionName = "v$id",
            versionCode = id,
            icon = id + 1,
            preInstalled = (id % 2 == 0),
            lastUpdateTime = id + 2L
    )
}