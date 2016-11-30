package com.droibit.quickly.packages

import rx.Single

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

        fun performPackageAction(action: Action, packageName: String)
    }

    interface PackageTask {

        fun add(packageName: String): Single<Boolean>

        fun update(packageName: String): Single<Boolean>

        fun remove(packageName: String): Single<Boolean>
    }
}