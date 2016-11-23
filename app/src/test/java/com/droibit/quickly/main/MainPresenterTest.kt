package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.MenuItem
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.Completable
import rx.Observable
import rx.Single
import rx.lang.kotlin.toSingletonObservable
import rx.subscriptions.CompositeSubscription

@Suppress("UNCHECKED_CAST")
class MainPresenterTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    private lateinit var view: MainContract.View

    @Mock
    private lateinit var loadTask: MainContract.LoadAppInfoTask

    @Mock
    private lateinit var showSettingsTask: MainContract.ShowSettingsTask

    private lateinit var subscriptions: CompositeSubscription

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = MainPresenter(
                view,
                loadTask,
                showSettingsTask,
                subscriptions
        )
    }

    @Test
    fun onCreate_setSortBy() {
        val result = Pair(SortBy.LAST_UPDATED, Order.DESC)
        `when`(showSettingsTask.loadSortBy()).thenReturn(Single.just(result))

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
            verify(view).showAppInfoList(mockList)
        }

        run {
            `when`(mockList.isEmpty()).thenReturn(true)

            presenter.onResume()
            verify(view).showNoAppInfo()
        }
    }

    @Test
    fun onPrepareShowSystemMenu_setShowSystem() {
        `when`(showSettingsTask.loadShowSystem()).thenReturn(Single.just(true))

        presenter.onPrepareShowSystemMenu()
        verify(view).setShowSystem(true)
    }

    @Test
    fun onOptionsItemClicked_showAppInfoList() {
        `when`(showSettingsTask.storeShowSystem(anyBoolean())).thenReturn(Completable.complete())

        val mockList = mock(List::class.java) as List<AppInfo>
        `when`(mockList.isEmpty()).thenReturn(false)
        `when`(loadTask.requestLoad()).thenReturn(mockList.toSingletonObservable())

        presenter.onOptionsItemClicked(MenuItem.ShowSystem(checked = true))

        verify(view).showAppInfoList(mockList)
    }

    @Test
    fun onSortByClicked_showSortByChooserDialog() {
        val result = Pair(SortBy.NAME, Order.ASC)
        `when`(showSettingsTask.loadSortBy()).thenReturn(Single.just(result))

        presenter.onSortByClicked()
        verify(view).showSortByChooserDialog(sortBy = result.first)
    }

    @Test
    fun onSortByChoose_setSortBy() {
        val result = Pair(SortBy.LAST_UPDATED, Order.DESC)
        `when`(showSettingsTask.storeSortBy(result.first, result.second)).thenReturn(Single.just(true))

        presenter.onSortByChoose(result.first, result.second)
        verify(view).setSortBy(result.first, result.second)
    }
}