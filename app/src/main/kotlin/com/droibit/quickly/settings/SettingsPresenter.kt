package com.droibit.quickly.settings

class SettingsPresenter(private val navigator: SettingsContract.Navigator)
    : SettingsContract.Presenter {

    override fun onOpenSourceLicensesClicked() {
        navigator.startOpenSourceLicenses()
    }

    override fun onHomeClicked() {
        navigator.finish()
    }
}