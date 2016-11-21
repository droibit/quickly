package com.droibit.quickly.main

import com.droibit.quickly.data.repository.settings.ShowSettingsRepository
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import rx.observers.TestSubscriber

class SortByTaskTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Mock
    private lateinit var showSettingsRepository: ShowSettingsRepository

    private lateinit var task: SortByTask

    @Before
    fun setUp() {
        task = SortByTask(showSettingsRepository)
    }

    @Test
    fun load_getSortByWithOrder() {
        `when`(showSettingsRepository.sortBy).thenReturn(SortBy.LAST_UPDATED)
        `when`(showSettingsRepository.order).thenReturn(Order.DESC)

        val testSubscriber = TestSubscriber.create<Pair<SortBy, Order>>()
        task.load().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(Pair(SortBy.LAST_UPDATED, Order.DESC))
    }

    @Test
    fun store_setSortByWithOrder() {
        // updated sort by
        run {
            `when`(showSettingsRepository.sortBy).thenReturn(SortBy.LAST_UPDATED)
            `when`(showSettingsRepository.order).thenReturn(Order.ASC)

            val testSubscriber = TestSubscriber.create<Boolean>()
            task.store(SortBy.PACKAGE, Order.ASC).subscribe(testSubscriber)

            testSubscriber.assertValues(true)

            verify(showSettingsRepository).sortBy = SortBy.PACKAGE
            verify(showSettingsRepository).order = Order.ASC
        }
        reset(showSettingsRepository)

        // updated order
        run {
            `when`(showSettingsRepository.sortBy).thenReturn(SortBy.PACKAGE)
            `when`(showSettingsRepository.order).thenReturn(Order.ASC)

            val testSubscriber = TestSubscriber.create<Boolean>()
            task.store(SortBy.PACKAGE, Order.DESC).subscribe(testSubscriber)

            testSubscriber.assertValues(true)

            verify(showSettingsRepository).sortBy = SortBy.PACKAGE
            verify(showSettingsRepository).order = Order.DESC
        }
        reset(showSettingsRepository)

        // no updated
        run {
            `when`(showSettingsRepository.sortBy).thenReturn(SortBy.PACKAGE)
            `when`(showSettingsRepository.order).thenReturn(Order.ASC)

            val testSubscriber = TestSubscriber.create<Boolean>()
            task.store(SortBy.PACKAGE, Order.ASC).subscribe(testSubscriber)

            testSubscriber.assertValues(false)

            verify(showSettingsRepository).sortBy = SortBy.PACKAGE
            verify(showSettingsRepository).order = Order.ASC
        }
    }
}