package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import com.jakewharton.rxrelay.BehaviorRelay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.Observable
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

    private lateinit var subscriptions: CompositeSubscription

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        subscriptions = CompositeSubscription()
        presenter = MainPresenter(
                view,
                loadTask,
                subscriptions
        )
    }

    @Test
    fun onCreate_requestLoad() {
        presenter.onCreate(shouldLoad = true)
        verify(loadTask).requestLoad(true)

        presenter.onCreate(shouldLoad = false)
        verify(loadTask).requestLoad(false)
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
    fun onResume_subscribeAppInfos() {
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
    fun onResume_resubscribeAppInfos() {
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
}