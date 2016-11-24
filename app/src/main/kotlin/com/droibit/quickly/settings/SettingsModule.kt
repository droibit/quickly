package com.droibit.quickly.settings

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider

fun settingsModule(navigator: SettingsContract.Navigator) = Kodein.Module {

    bind<SettingsContract.Navigator>() with instance(navigator)

    bind<SettingsContract.Presenter>() with provider {
        SettingsPresenter(navigator = instance())
    }
}