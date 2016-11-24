package com.droibit.quickly.settings

interface SettingsContract {

    interface Navigator {

        fun startOpenSourceLicenses()

        fun finish()
    }

    interface Presenter {

        fun onOpenSourceLicensesClicked()

        fun onHomeClicked()
    }
}