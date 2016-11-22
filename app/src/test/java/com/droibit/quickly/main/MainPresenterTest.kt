package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import com.jakewharton.rxrelay.BehaviorRelay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
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
    private lateinit var sortByTask: MainContract.SortByTask

    private lateinit var subscriptions: CompositeSubscription

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = MainPresenter(
                view,
                loadTask,
                sortByTask,
                subscriptions
        )
    }

    @Test
    fun onCreate_requestLoad() {
        `when`(sortByTask.load()).thenReturn(Single.just(Pair(SortBy.NAME, Order.ASC)))

        presenter.onCreate(shouldLoad = true)
        verify(loadTask).requestLoad(true)

        presenter.onCreate(shouldLoad = false)
        verify(loadTask).requestLoad(false)
    }

    @Test
    fun onCreate_setSortBy() {
        val result = Pair(SortBy.LAST_UPDATED, Order.DESC)
        `when`(sortByTask.load()).thenReturn(Single.just(result))

        presenter.onCreate(true)
        verify(view).setSortBy(result.first, result.second)
    }

    @Test
    fun onResume_subscribeRunning() {
        `when`(loadTask.asObservable()).thenReturn(Observable.empty())

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
        val relay = BehaviorRelay.create<LoadEvent>()
        `when`(loadTask.asObservable()).thenReturn(relay)

        run {
            `when`(mockList.isEmpty()).thenReturn(false)

            presenter.onResume()
            relay.call(LoadEvent.OnResult(mockList))
            verify(view).showAppInfoList(mockList)
        }

        run {
            reset(view)
            presenter.onResume()
            verify(view, never()).showAppInfoList(anyListOf(AppInfo::class.java))
        }

        run {
            `when`(mockList.isEmpty()).thenReturn(true)

            presenter.onResume()
            relay.call(LoadEvent.OnResult(mockList))
            verify(view).showNoAppInfo()
        }
    }

    @Test
    fun onResume_resubscribeApps() {
        `when`(loadTask.isRunning()).thenReturn(Observable.empty())

        val relay = BehaviorRelay.create<LoadEvent>()
        `when`(loadTask.asObservable()).thenReturn(relay)

        presenter.onResume()
        presenter.onPause()

        val mockList = mock(List::class.java) as List<AppInfo>
        relay.call(LoadEvent.OnResult(mockList))

        presenter.onResume()
        verify(view).showAppInfoList(mockList)
    }

    @Test
    fun onSortByClicked_showSortByChooserDialog() {
        val result = Pair(SortBy.NAME, Order.ASC)
        `when`(sortByTask.load()).thenReturn(Single.just(result))

        presenter.onSortByClicked()
        verify(view).showSortByChooserDialog(sortBy = result.first)
    }

    @Test
    fun onSortByChoose_setSortBy() {
        val result = Pair(SortBy.LAST_UPDATED, Order.DESC)
        `when`(sortByTask.store(result.first, result.second)).thenReturn(Single.just(true))

        presenter.onSortByChoose(result.first, result.second)
        verify(view).setSortBy(result.first, result.second)
    }
}