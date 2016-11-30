package com.droibit.quickly.packages

interface PackageContract {

    enum class Action {
        PACKAGE_ADDED,
        PACKAGE_REPLACED,
        PACKAGE_REMOVED
    }

    interface Receiver {

        fun performPackageAction(action: Action, packageName: String)
    }

    interface ActionHandler {

        fun onPackageAdded(packageName: String)

        fun onPackageReplaced(packageName: String)

        fun onPackageRemoved(packageName: String, replacing: Boolean)
    }
}