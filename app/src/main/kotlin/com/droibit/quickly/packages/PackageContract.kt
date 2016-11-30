package com.droibit.quickly.packages

interface PackageContract {

    interface ActionHandler {

        fun onPackageAdded(packageName: String)

        fun onPackageReplaced(packageName: String)

        fun onPackageRemoved(packageName: String, replacing: Boolean)
    }
}