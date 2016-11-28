package com.droibit.quickly.data.provider.intent

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS

object IntentCreatorImpl : IntentCreator {

    override fun newUninstallIntent(packageName: String): Intent {
        return Intent(ACTION_UNINSTALL_PACKAGE, packageName.toUri())
                .putExtra(EXTRA_RETURN_RESULT, true)
    }

    override fun newShareIntent(text: String): Intent {
        return Intent(ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
    }

    override fun newAppInfoIntent(packageName: String): Intent {
        return Intent(ACTION_APPLICATION_DETAILS_SETTINGS, packageName.toUri())
    }
}

private fun String.toUri() = Uri.fromParts("package", this, null)