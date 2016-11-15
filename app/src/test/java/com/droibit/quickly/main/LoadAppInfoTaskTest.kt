package com.droibit.quickly.main

import com.droibit.quickly.data.config.ApplicationConfig
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.main.MainContract.LoadAppInfoTask.LoadEvent
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import com.jakewharton.rxrelay.BehaviorRelay
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class LoadAppInfoTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Rule
    @JvmField
    val overrideSchedulers = RxSchedulersOverrideRule()

    @Mock
    private lateinit var appInfoRepository: AppInfoRepository

    @Mock
    private lateinit var appConfig: ApplicationConfig

    @Mock
    lateinit var runningRelay: BehaviorRelay<Boolean>

    private lateinit var appInfosRelay: BehaviorRelay<LoadEvent>

    private lateinit var task: LoadAppInfoTask

    @Before
    fun setUp() {
        appInfosRelay = BehaviorRelay.create()
        task = LoadAppInfoTask(
                appInfoRepository,
                appConfig,
                appInfosRelay,
                runningRelay
        )
    }

    @Test
    fun load_onResult() {
        val mockAppInfos = Collections.singletonList(mock(AppInfo::class.java))
        `when`(appInfoRepository.loadAll(anyBoolean())).thenReturn(mockAppInfos.toSingletonObservable())

        val testSubscriber = TestSubscriber.create<LoadEvent>()
        task.asObservable().subscribe(testSubscriber)
        task.load(forceReload = true)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)

        val loadEvent = testSubscriber.onNextEvents.first()
        assertThat(loadEvent).isExactlyInstanceOf(LoadEvent.OnResult::class.java)

        val onResult = loadEvent as LoadEvent.OnResult
        assertThat(onResult.appInfos).isSameAs(mockAppInfos)

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }

    @Test
    fun load_onResultWithError() {
        `when`(appInfoRepository.loadAll(anyBoolean())).thenReturn(Observable.error(RuntimeException()))

        val testSubscriber = TestSubscriber.create<LoadEvent>()
        task.asObservable().subscribe(testSubscriber)
        task.load(forceReload = true)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)

        val loadEvent = testSubscriber.onNextEvents.first()
        assertThat(loadEvent).isExactlyInstanceOf(LoadEvent.OnResult::class.java)

        val onResult = loadEvent as LoadEvent.OnResult
        assertThat(onResult.appInfos).isSameAs(Collections.emptyList<AppInfo>())

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }
}