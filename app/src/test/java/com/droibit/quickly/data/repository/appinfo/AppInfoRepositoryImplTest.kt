package com.droibit.quickly.data.repository.appinfo

import android.os.Build
import com.droibit.quickly.BuildConfig
import com.droibit.quickly.data.repository.source.AppInfoDataSource
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import rx.Observable
import rx.lang.kotlin.singleOf
import rx.lang.kotlin.toObservable
import rx.observers.TestSubscriber

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class AppInfoRepositoryImplTest {

    private val context = RuntimeEnvironment.application

    private lateinit var orma: OrmaDatabase

    private lateinit var appInfoSource: AppInfoDataSource

    private lateinit var repository: AppInfoRepositoryImpl

    @Before
    fun setUp() {
        orma = OrmaDatabase.Builder(context).name(null).build()
        appInfoSource = mock()
        repository = AppInfoRepositoryImpl(orma, appInfoSource)
    }

    @Test
    fun loadAll_shouldReturnLoadedAppInfo() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = true,
                        lastUpdateTime = 4
                ),
                AppInfo(
                        packageName = "com.droibit.quickly.2",
                        name = "Qickly2",
                        versionName = "5",
                        versionCode = 6,
                        icon = 7,
                        preInstalled = true,
                        lastUpdateTime = 8
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
        whenever(appInfoSource.getAll()).thenReturn(expectedAppInfoList.toObservable())

        val testSubscriber = TestSubscriber.create<List<AppInfo>>()
        repository.loadAll().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)

        val actualAppInfoList = testSubscriber.onNextEvents.first()
        assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)
    }

    @Test
    fun loadAll_shouldReturnStoredAppInfo() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = true,
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

        expectedAppInfoList.forEach {
            whenever(appInfoSource.get(it.packageName)).thenReturn(singleOf(it))

            repository.addOrUpdate(packageName = it.packageName)
                    .subscribe { assertThat(it).isTrue() }
        }

        val notCallAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.2",
                        name = "Qickly2",
                        versionName = "5",
                        versionCode = 6,
                        icon = 7,
                        preInstalled = true,
                        lastUpdateTime = 8
                )
        )
        whenever(appInfoSource.getAll()).thenReturn(notCallAppInfoList.toObservable())

        val testSubscriber = TestSubscriber.create<List<AppInfo>>()
        repository.loadAll().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)

        val actualAppInfoList = testSubscriber.onNextEvents.first()
        assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)

        assertThat(repository.cache.keys).hasSize(2)
    }

    @Test
    fun loadAll_shouldReturnCachedAppInfo() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = true,
                        lastUpdateTime = 4
                ),
                AppInfo(
                        packageName = "com.droibit.quickly.2",
                        name = "Qickly2",
                        versionName = "5",
                        versionCode = 6,
                        icon = 7,
                        preInstalled = true,
                        lastUpdateTime = 8
                )
        )
        whenever(appInfoSource.getAll()).thenReturn(expectedAppInfoList.toObservable())
        repository.loadAll().subscribe()

        whenever(appInfoSource.getAll()).thenReturn(Observable.empty())
        assertThat(orma.deleteFromAppInfo().execute()).isEqualTo(2)

        val testSubscriber = TestSubscriber.create<List<AppInfo>>()
        repository.loadAll().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)

        val actualAppInfoList = testSubscriber.onNextEvents.first()
        assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)
    }

    @Test
    fun loadAll_thenForceReloadAll() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = true,
                        lastUpdateTime = 4
                ),
                AppInfo(
                        packageName = "com.droibit.quickly.2",
                        name = "Qickly2",
                        versionName = "5",
                        versionCode = 6,
                        icon = 7,
                        preInstalled = true,
                        lastUpdateTime = 8
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
        whenever(appInfoSource.getAll()).thenReturn(expectedAppInfoList.toObservable())

        run {
            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            repository.loadAll().subscribe(testSubscriber)

            testSubscriber.assertNoErrors()
            testSubscriber.assertCompleted()
            testSubscriber.assertValueCount(1)

            val actualAppInfoList = testSubscriber.onNextEvents.first()
            assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)
        }

        run {
            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            repository.loadAll(forceReload = true).subscribe(testSubscriber)

            testSubscriber.assertNoErrors()
            testSubscriber.assertCompleted()
            testSubscriber.assertValueCount(1)

            val actualAppInfoList = testSubscriber.onNextEvents.first()
            assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)
        }
    }

    @Test
    fun roadAll_shouldReturnAppInfoWithForceReload() {
        run {
            val appInfo = AppInfo(
                    packageName = "com.droibit.quickly.1",
                    name = "Qickly1",
                    versionName = "1",
                    versionCode = 2,
                    icon = 3,
                    preInstalled = true,
                    lastUpdateTime = 4
            )
            whenever(appInfoSource.getAll()).thenReturn(listOf(appInfo).toObservable())

            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            repository.loadAll().subscribe(testSubscriber)

            testSubscriber.assertNoErrors()
            testSubscriber.assertCompleted()
            testSubscriber.assertValueCount(1)

            val actualAppInfoList = testSubscriber.onNextEvents.first()
            assertThat(actualAppInfoList).containsExactly(appInfo)
        }

        run {
            val expectedAppInfoList = listOf(
                    AppInfo(
                            packageName = "com.droibit.quickly.2",
                            name = "Qickly2",
                            versionName = "5",
                            versionCode = 6,
                            icon = 7,
                            preInstalled = true,
                            lastUpdateTime = 8
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
            whenever(appInfoSource.getAll()).thenReturn(expectedAppInfoList.toObservable())

            val testSubscriber = TestSubscriber.create<List<AppInfo>>()
            repository.loadAll(forceReload = true).subscribe(testSubscriber)

            testSubscriber.assertNoErrors()
            testSubscriber.assertCompleted()
            testSubscriber.assertValueCount(1)

            val actualAppInfoList = testSubscriber.onNextEvents.first()
            assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)
        }
    }

    @Test
    fun addOrUpdate_shouldAddNewAppInfo() {
        val expectedAppInfoList = listOf(
                AppInfo(
                        packageName = "com.droibit.quickly.1",
                        name = "Qickly1",
                        versionName = "1",
                        versionCode = 2,
                        icon = 3,
                        preInstalled = true,
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
        expectedAppInfoList.forEach { appInfo ->
            whenever(appInfoSource.get(appInfo.packageName)).thenReturn(singleOf(appInfo))

            repository.addOrUpdate(appInfo.packageName)
                    .subscribe { added ->
                        assertThat(added).isTrue()
                        assertThat(repository.cache).isEmpty()
                    }
        }
        assertThat(orma.selectFromAppInfo().toList()).containsExactlyElementsOf(expectedAppInfoList)
    }

    @Test
    fun addOrUpdate_shouldUpdatedExistAppInfo() {
        AppInfo(
                packageName = "com.droibit.quickly.1",
                name = "Qickly1",
                versionName = "1",
                versionCode = 2,
                icon = 3,
                preInstalled = true,
                lastUpdateTime = 4
        ).apply {
            whenever(appInfoSource.get(packageName)).thenReturn(singleOf(this))

            repository.addOrUpdate(packageName)
                    .subscribe { assertThat(it).isTrue() }
            assertThat(orma.selectFromAppInfo().toList()).containsExactly(this)
            assertThat(repository.cache).isEmpty()
        }

        AppInfo(
                packageName = "com.droibit.quickly.1",
                name = "Qickly1",
                versionName = "2",
                versionCode = 3,
                icon = 3,
                preInstalled = true,
                lastUpdateTime = 5
        ).run {
            whenever(appInfoSource.get(packageName)).thenReturn(singleOf(this))

            repository.addOrUpdate(packageName)
                    .subscribe { assertThat(it).isTrue() }
            assertThat(orma.selectFromAppInfo().toList()).containsExactly(this)
            assertThat(repository.cache).isEmpty()
        }
    }

    @Test
    fun delete_shouldDeleteExistAppInfo() {
        val appInfo = AppInfo(
                packageName = "com.droibit.quickly.1",
                name = "Qickly1",
                versionName = "1",
                versionCode = 2,
                icon = 3,
                preInstalled = true,
                lastUpdateTime = 4
        ).apply {
            whenever(appInfoSource.get(packageName)).thenReturn(singleOf(this))

            repository.addOrUpdate(packageName)
                    .subscribe { assertThat(it).isTrue() }
        }
        assertThat(orma.selectFromAppInfo().toList()).containsExactly(appInfo)

        repository.delete(appInfo.packageName)
                .subscribe { assertThat(it).isTrue() }
        assertThat(repository.cache).doesNotContainKey(appInfo.packageName)

        // has removed
        repository.delete(appInfo.packageName)
                .subscribe { assertThat(it).isFalse() }
    }
}