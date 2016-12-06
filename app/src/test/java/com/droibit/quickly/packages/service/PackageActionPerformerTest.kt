package com.droibit.quickly.packages.service

import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.packages.PackageContract.Action.PACKAGE_ADDED
import com.droibit.quickly.packages.PackageContract.Action.PACKAGE_REMOVED
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import rx.lang.kotlin.singleOf

class PackageActionPerformerTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Mock
    private lateinit var appInfoRepository: AppInfoRepository

    @Mock
    private lateinit var appEventBus: RxBus

    private lateinit var performer: PackageActionPerformer

    @Before
    fun setUp() {
        performer = PackageActionPerformer(appInfoRepository, appEventBus)
    }

    @Test
    fun performPackageAction_notifyAfterAddOrUpdate() {
        // success & hasObservers
        whenever(appInfoRepository.addOrUpdate(any())).thenReturn(singleOf(true))
        whenever(appEventBus.hasObservers).thenReturn(true)

        performer.performPackageAction(PACKAGE_ADDED, "add")
        verify(appEventBus).call(any())
    }

    @Test
    fun performPackageAction_doesNotNotifyAfterAddOrUpdate() {
        // failed
        run {
            whenever(appInfoRepository.addOrUpdate(any())).thenReturn(singleOf(false))
            whenever(appEventBus.hasObservers).thenReturn(true)

            performer.performPackageAction(PACKAGE_ADDED, "update")
            verify(appEventBus, never()).call(any())
        }
        reset(appEventBus)

        // success & hasNotObservers
        run {
            // success & hasObservers
            whenever(appInfoRepository.addOrUpdate(any())).thenReturn(singleOf(true))
            whenever(appEventBus.hasObservers).thenReturn(false)

            performer.performPackageAction(PACKAGE_ADDED, "update")
            verify(appEventBus, never()).call(any())
        }
    }

    @Test
    fun performPackageAction_notifyAfterDelete() {
        // success & hasObservers
        whenever(appInfoRepository.delete(any())).thenReturn(singleOf(true))
        whenever(appEventBus.hasObservers).thenReturn(true)

        performer.performPackageAction(PACKAGE_REMOVED, "delete")
        verify(appEventBus).call(any())
    }

    @Test
    fun performPackageAction_doesNotNotifyAfterDelete() {
        // failed
        run {
            whenever(appInfoRepository.delete(any())).thenReturn(singleOf(false))
            whenever(appEventBus.hasObservers).thenReturn(true)

            performer.performPackageAction(PACKAGE_REMOVED, "delete")
            verify(appEventBus, never()).call(any())
        }
        reset(appEventBus)

        // success & hasNotObservers
        run {
            // success & hasObservers
            whenever(appInfoRepository.delete(any())).thenReturn(singleOf(true))
            whenever(appEventBus.hasObservers).thenReturn(false)

            performer.performPackageAction(PACKAGE_REMOVED, "delete")
            verify(appEventBus, never()).call(any())
        }
    }
}