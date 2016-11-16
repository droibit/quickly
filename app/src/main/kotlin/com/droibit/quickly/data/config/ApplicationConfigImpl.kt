package com.droibit.quickly.data.config

import java.util.concurrent.TimeUnit

class ApplicationConfigImpl : ApplicationConfig {

    override val minTaskDurationMillis = TimeUnit.SECONDS.toMillis(1)

    override val showSettingsPrefsName = "show_settings"
}