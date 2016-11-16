package com.droibit.quickly.data.repository.appinfo

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.droibit.quickly.data.repository.source.AppInfoDataSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.Observable
import rx.Single
import rx.observers.TestSubscriber

@RunWith(AndroidJUnit4::class)
class AppInfoRepositoryImplTest {

    private val context = InstrumentationRegistry.getTargetContext()

    private lateinit var orma: OrmaDatabase

    private lateinit var appInfoSource: AppInfoDataSource

    private lateinit var repository: AppInfoRepositoryImpl

    @Before
    fun setUp() {
        orma = OrmaDatabase.Builder(context).name(null).build()
        appInfoSource = mock(AppInfoDataSource::class.java)
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
        `when`(appInfoSource.getAll()).thenReturn(Observable.just(expectedAppInfoList))

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
            `when`(appInfoSource.get(it.packageName)).thenReturn(Single.just(it))

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
        `when`(appInfoSource.getAll()).thenReturn(Observable.just(notCallAppInfoList))

        val testSubscriber = TestSubscriber.create<List<AppInfo>>()
        repository.loadAll().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)

        val actualAppInfoList = testSubscriber.onNextEvents.first()
        assertThat(actualAppInfoList).containsExactlyElementsOf(expectedAppInfoList)
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
        `when`(appInfoSource.getAll()).thenReturn(Observable.just(expectedAppInfoList))
        repository.loadAll().subscribe()

        `when`(appInfoSource.getAll()).thenReturn(Observable.empty())
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
            `when`(appInfoSource.getAll()).thenReturn(Observable.just(listOf(appInfo)))

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
            `when`(appInfoSource.getAll()).thenReturn(Observable.just(expectedAppInfoList))

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
            `when`(appInfoSource.get(appInfo.packageName)).thenReturn(Single.just(appInfo))

            repository.addOrUpdate(appInfo.packageName)
                    .subscribe { added ->
                        assertThat(added).isTrue()
                        assertThat(repository.cache).containsEntry(appInfo.packageName, appInfo)
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
            `when`(appInfoSource.get(packageName)).thenReturn(Single.just(this))

            repository.addOrUpdate(packageName)
                    .subscribe { assertThat(it).isTrue() }
            assertThat(orma.selectFromAppInfo().toList()).containsExactly(this)
            assertThat(repository.cache).containsEntry(packageName, this)
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
            `when`(appInfoSource.get(packageName)).thenReturn(Single.just(this))

            repository.addOrUpdate(packageName)
                    .subscribe { assertThat(it).isTrue() }
            assertThat(orma.selectFromAppInfo().toList()).containsExactly(this)
            assertThat(repository.cache).containsEntry(packageName, this)
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
            `when`(appInfoSource.get(packageName)).thenReturn(Single.just(this))

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