package com.droibit.quickly.settings

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import android.view.View
import com.droibit.quickly.BuildConfig
import com.droibit.quickly.R
import com.github.droibit.chopstick.bindPreference
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance

class SettingsFragment : PreferenceFragmentCompat(), SettingsContract.Navigator {

    private val ossPref: Preference by bindPreference(R.string.settings_pref_about_oss_key)

    private val versionPref: Preference by bindPreference(R.string.settings_pref_about_app_version_key)

    private val injector = KodeinInjector()

    private val presenter: SettingsContract.Presenter by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injector.inject(Kodein {
            extend(appKodein())

            import(settingsModule(navigator = this@SettingsFragment))
        })
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        versionPref.summary = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        ossPref.setOnPreferenceClickListener {
            presenter.onOpenSourceLicensesClicked(); true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { presenter.onHomeClicked(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // SettingsContract.Navigator

    override fun startOpenSourceLicenses() {
    }

    override fun finish() {
        activity?.finish()
    }
}