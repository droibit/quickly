package com.droibit.quickly.main.apps

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract
import com.droibit.quickly.main.apps.AppsContract
import com.droibit.quickly.main.apps.AppsContract.MenuItem
import com.droibit.quickly.main.apps.AppsPresenter
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.Completable
import rx.Observable
import rx.Single
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
        `when`(showSettingsTask.loadSortBy()).thenReturn(singleOf(result))

        presenter.onCreate(true)
        verify(view).setSortBy(result.first, result.second)
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

        val mockList = mock(List::class.java) as List<AppInfo>
        `when`(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())

        run {
            `when`(mockList.isEmpty()).thenReturn(false)

            presenter.onResume()
            verify(view).showApps(mockList)
        }

        run {
            `when`(mockList.isEmpty()).thenReturn(true)

            presenter.onResume()
            verify(view).showNoAppInfo()
        }
    }

    @Test
    fun onPrepareShowSystemMenu_setShowSystem() {
        `when`(showSettingsTask.loadShowSystem()).thenReturn(singleOf(true))

        presenter.onPrepareShowSystemMenu()
        verify(view).setShowSystem(true)
    }

    @Test
    fun onOptionsItemClicked_showSystem_showApps() {
        `when`(showSettingsTask.storeShowSystem(anyBoolean())).thenReturn(Completable.complete())

        val mockList = mock(List::class.java) as List<AppInfo>
        `when`(mockList.isEmpty()).thenReturn(false)
        `when`(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())

        presenter.onOptionsItemClicked(MenuItem.ShowSystem(checked = true))

        verify(view).showApps(mockList, true)
    }

    @Test
    fun onOptionsItemClicked_refresh_showApps() {
        val mockList = mock(List::class.java) as List<AppInfo>
        `when`(mockList.isEmpty()).thenReturn(false)
        `when`(loadTask.requestLoad(anyBoolean())).thenReturn(mockList.toSingletonObservable())

        presenter.onOptionsItemClicked(MenuItem.Refresh)

        verify(view).showApps(mockList, true)
    }

    @Test
    fun onOptionsItemClicked_settings_navigateSettings() {
        presenter.onOptionsItemClicked(MenuItem.Settings)

        verify(navigator).navigateSettings()
    }

    @Test
    fun onSortByClicked_showSortByChooserDialog() {
        val result = Pair(SortBy.NAME, Order.ASC)
        `when`(showSettingsTask.loadSortBy()).thenReturn(singleOf(result))

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
        `when`(showSettingsTask.storeSortBy(result.first, result.second)).thenReturn(singleOf(true))

        presenter.onSortByChoose(result.first, result.second)
        verify(view).setSortBy(result.first, result.second)
    }
}