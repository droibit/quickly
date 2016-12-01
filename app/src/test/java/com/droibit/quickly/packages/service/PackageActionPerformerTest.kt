package com.droibit.quickly.packages.service

import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.appinfo.AppInfoContract
import com.droibit.quickly.data.repository.appinfo.AppInfoRepository
import com.droibit.quickly.packages.PackageContract
import com.droibit.quickly.packages.PackageContract.Action.PACKAGE_ADDED
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
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
    fun test() {
        // TODO()
    }
}