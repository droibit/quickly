package com.droibit.quickly.packages.receiver

import com.droibit.quickly.packages.PackageContract
import com.droibit.quickly.packages.PackageContract.Action.*
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class PackageActionHandlerTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    @Mock
    private lateinit var receiver: PackageContract.Receiver

    private lateinit var handler: PackageActionHandler

    @Before
    fun setUp() {
        handler = PackageActionHandler(receiver)
    }

    @Test
    fun onPackageAdded_startPackageAction() {
        val packageName = "add"
        handler.onPackageAdded(packageName)

        verify(receiver).startPackageAction(PACKAGE_ADDED, packageName)
    }

    @Test
    fun onPackageReplaced_startPackageAction() {
        val packageName = "replaced"
        handler.onPackageReplaced(packageName)

        verify(receiver).startPackageAction(PACKAGE_REPLACED, packageName)
    }

    @Test
    fun onPackageRemoved_startPackageAction() {
        // not replacing
        run {
            val packageName = "not_replacing"
            handler.onPackageRemoved(packageName, replacing = false)

            verify(receiver).startPackageAction(PACKAGE_REMOVED, packageName)
        }
        reset(receiver)

        // replacing
        run {
            val packageName = "not_replacing"
            handler.onPackageRemoved(packageName, replacing = true)

            verify(receiver, never()).startPackageAction(PACKAGE_REMOVED, packageName)
        }
    }

}