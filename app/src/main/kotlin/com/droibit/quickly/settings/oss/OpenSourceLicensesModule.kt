package com.droibit.quickly.settings.oss

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun openSourceLicensesModule(navigator: OpenSourceLicensesContract.Navigator) = Kodein.Module {

    bind<OpenSourceLicensesContract.Navigator>() with instance(navigator)

    bind<OpenSourceLicensesContract.Presenter>() with provider {
        OpenSourceLicensesPresenter(navigator = instance())
    }
}