package com.droibit.quickly.packages

import android.support.annotation.WorkerThread

interface PackageContract {

    enum class Action {
        PACKAGE_ADDED,
        PACKAGE_REPLACED,
        PACKAGE_REMOVED
    }

    interface Receiver {

        fun startPackageAction(action: Action, packageName: String)
    }

    interface ActionHandler {

        fun onPackageAdded(packageName: String)

        fun onPackageReplaced(packageName: String)

        fun onPackageRemoved(packageName: String, replacing: Boolean)
    }

    interface ActionPerformer {

        @WorkerThread
        fun performPackageAction(action: Action, packageName: String)
    }
}