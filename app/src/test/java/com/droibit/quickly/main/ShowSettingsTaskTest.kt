package com.droibit.quickly.main

import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber

class ShowSettingsTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Mock
    private lateinit var showSettingsRepository: ShowSettingsRepository

    private lateinit var task: ShowSettingsTask

    @Before
    fun setUp() {
        task = ShowSettingsTask(showSettingsRepository)
    }

    @Test
    fun load_sortByWithOrder() {
        whenever(showSettingsRepository.sortBy).thenReturn(SortBy.LAST_UPDATED)
        whenever(showSettingsRepository.order).thenReturn(Order.DESC)

        val testSubscriber = TestSubscriber.create<Pair<SortBy, Order>>()
        task.loadSortBy().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(Pair(SortBy.LAST_UPDATED, Order.DESC))
    }

    @Test
    fun store_sortByWithOrder() {
        // updated sort by
        run {
            whenever(showSettingsRepository.sortBy).thenReturn(SortBy.LAST_UPDATED)
            whenever(showSettingsRepository.order).thenReturn(Order.ASC)

            val testSubscriber = TestSubscriber.create<Boolean>()
            task.storeSortBy(SortBy.PACKAGE, Order.ASC).subscribe(testSubscriber)

            testSubscriber.assertValues(true)

            verify(showSettingsRepository).sortBy = SortBy.PACKAGE
            verify(showSettingsRepository).order = Order.ASC
        }
        reset(showSettingsRepository)

        // updated order
        run {
            whenever(showSettingsRepository.sortBy).thenReturn(SortBy.PACKAGE)
            whenever(showSettingsRepository.order).thenReturn(Order.ASC)

            val testSubscriber = TestSubscriber.create<Boolean>()
            task.storeSortBy(SortBy.PACKAGE, Order.DESC).subscribe(testSubscriber)

            testSubscriber.assertValues(true)

            verify(showSettingsRepository).sortBy = SortBy.PACKAGE
            verify(showSettingsRepository).order = Order.DESC
        }
        reset(showSettingsRepository)

        // no updated
        run {
            whenever(showSettingsRepository.sortBy).thenReturn(SortBy.PACKAGE)
            whenever(showSettingsRepository.order).thenReturn(Order.ASC)

            val testSubscriber = TestSubscriber.create<Boolean>()
            task.storeSortBy(SortBy.PACKAGE, Order.ASC).subscribe(testSubscriber)

            testSubscriber.assertValues(false)

            verify(showSettingsRepository).sortBy = SortBy.PACKAGE
            verify(showSettingsRepository).order = Order.ASC
        }
    }

    @Test
    fun load_showSystem() {
        whenever(showSettingsRepository.isShowSystem).thenReturn(true)

        val testSubscriber = TestSubscriber.create<Boolean>()
        task.loadShowSystem().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(true)
    }

    @Test
    fun store_showSystem() {

        val testSubscriber = TestSubscriber.create<Unit>()
        task.storeShowSystem(true).subscribe(testSubscriber)

        testSubscriber.assertCompleted()

        verify(showSettingsRepository).isShowSystem = true
    }
}