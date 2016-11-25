package com.droibit.quickly.settings.oss

class OpenSourceLicensesPresenter(private val navigator: OpenSourceLicensesContract.Navigator)
    : OpenSourceLicensesContract.Presenter {

    override fun onHomeClicked() {
        navigator.finish()
    }
}