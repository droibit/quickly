package com.droibit.quickly.main

import com.droibit.quickly.data.config.ApplicationConfig
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
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
    private lateinit var showSettingsRepository: ShowSettingsRepository

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
                showSettingsRepository,
                appConfig,
                appInfosRelay,
                runningRelay
        )
    }

    @Test
    fun requestLoad_allApps_onResult() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = false,
                        lastUpdateTime = 4
                ),
                AppInfo(
                        packageName = "com.droibit.quickly.3",
                        name = "Qickly3",
                        versionName = "9",
                        versionCode = 10,
                        icon = 11,
                        preInstalled = true,
                        lastUpdateTime = 12
                )
        )
        `when`(appInfoRepository.loadAll(anyBoolean())).thenReturn(expectedAppInfoList.toSingletonObservable())
        `when`(showSettingsRepository.isShowSystem).thenReturn(true)

        val testSubscriber = TestSubscriber.create<LoadEvent>()
        task.asObservable().subscribe(testSubscriber)
        task.requestLoad(forceReload = true)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)

        val loadEvent = testSubscriber.onNextEvents.first()
        assertThat(loadEvent).isExactlyInstanceOf(LoadEvent.OnResult::class.java)

        val onResult = loadEvent as LoadEvent.OnResult
        assertThat(onResult.appInfos).isEqualTo(expectedAppInfoList)

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }

    @Test
    fun requestLoad_onlyInstalled_onResult() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = false,
                        lastUpdateTime = 4
                ),
                AppInfo(
                        packageName = "com.droibit.quickly.3",
                        name = "Qickly3",
                        versionName = "9",
                        versionCode = 10,
                        icon = 11,
                        preInstalled = true,
                        lastUpdateTime = 12
                )
        )
        `when`(appInfoRepository.loadAll(anyBoolean())).thenReturn(expectedAppInfoList.toSingletonObservable())
        `when`(showSettingsRepository.isShowSystem).thenReturn(false)

        val testSubscriber = TestSubscriber.create<LoadEvent>()
        task.asObservable().subscribe(testSubscriber)
        task.requestLoad(forceReload = true)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)

        val loadEvent = testSubscriber.onNextEvents.first()
        assertThat(loadEvent).isExactlyInstanceOf(LoadEvent.OnResult::class.java)

        val onResult = loadEvent as LoadEvent.OnResult
        assertThat(onResult.appInfos).isEqualTo(expectedAppInfoList.filter { !it.preInstalled })

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }

    @Test
    fun  requestLoad_occurError_onResult() {
        `when`(appInfoRepository.loadAll(anyBoolean())).thenReturn(Observable.error(RuntimeException()))

        val testSubscriber = TestSubscriber.create<LoadEvent>()
        task.asObservable().subscribe(testSubscriber)
        task.requestLoad(forceReload = true)

        testSubscriber.assertNoTerminalEvent()
        testSubscriber.assertValueCount(1)

        val loadEvent = testSubscriber.onNextEvents.first()
        assertThat(loadEvent).isExactlyInstanceOf(LoadEvent.OnResult::class.java)

        val onResult = loadEvent as LoadEvent.OnResult
        assertThat(onResult.appInfos).isEqualTo(Collections.emptyList<AppInfo>())

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }
}