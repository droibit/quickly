package com.droibit.quickly.main

import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.rules.RxSchedulersOverrideRule
import com.jakewharton.rxrelay.BehaviorRelay
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Matchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.junit.MockitoJUnit
import rx.lang.kotlin.toSingletonObservable
import rx.observers.TestSubscriber

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
    lateinit var runningRelay: BehaviorRelay<Boolean>

    private lateinit var task: LoadAppInfoTask

    @Before
    fun setUp() {
        task = LoadAppInfoTask(
                appInfoRepository,
                showSettingsRepository,
                runningRelay
        )
    }

    @Test
    fun requestLoad_allApps() {
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

        val testSubscriber = TestSubscriber.create<List<AppInfo>>()
        task.requestLoad().subscribe(testSubscriber)

        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)

        val apps = testSubscriber.onNextEvents.first()
        assertThat(apps).isEqualTo(expectedAppInfoList)

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }

    @Test
    fun requestLoad_onlyInstalledApps() {
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

        val testSubscriber = TestSubscriber.create<List<AppInfo>>()
        task.requestLoad().subscribe(testSubscriber)

        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)

        val apps = testSubscriber.onNextEvents.first()
        assertThat(apps).isEqualTo(expectedAppInfoList.filter { !it.preInstalled })

        val inOrder = inOrder(runningRelay)
        inOrder.verify(runningRelay).call(true)
        inOrder.verify(runningRelay).call(false)
    }

    @Test
    fun requestLoad_sameAsCache() {
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

        run {
            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            task.requestLoad().subscribe(testSubscriber)

            testSubscriber.assertCompleted()
            testSubscriber.assertValueCount(1)

            val apps = testSubscriber.onNextEvents.first()
            assertThat(apps).isEqualTo(expectedAppInfoList)
        }

        // hit cache!
        run {
            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            task.requestLoad().subscribe(testSubscriber)

            testSubscriber.assertCompleted()
            testSubscriber.assertNoValues()
        }

        // new apps
        `when`(showSettingsRepository.isShowSystem).thenReturn(false)
        run {
            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            task.requestLoad().subscribe(testSubscriber)

            testSubscriber.assertCompleted()
            testSubscriber.assertValueCount(1)

            val apps = testSubscriber.onNextEvents.first()
            assertThat(apps).containsExactly(expectedAppInfoList.first())
        }
    }
}