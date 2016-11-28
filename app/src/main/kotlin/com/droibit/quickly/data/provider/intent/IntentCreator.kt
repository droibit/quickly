package com.droibit.quickly.data.provider.intent

import android.content.Intent


interface IntentCreator {

    fun newUninstallIntent(packageName: String): Intent

    fun newShareIntent(text: String): Intent

    fun newAppInfoIntent(packageName: String): Intent
}