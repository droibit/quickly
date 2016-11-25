package com.droibit.quickly.settings.oss

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebView
import com.droibit.quickly.R
import com.droibit.quickly.data.config.ApplicationConfig
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance

class OpenSourceLicensesActivity : AppCompatActivity(), OpenSourceLicensesContract.Navigator {

    companion object {

        @JvmStatic
        fun createIntent(context: Context) = Intent(context, OpenSourceLicensesActivity::class.java)
    }

    private val webView: WebView by bindView(R.id.web)

    private val injector = KodeinInjector()

    private val appConfig: ApplicationConfig by injector.instance()

    private val presenter: OpenSourceLicensesContract.Presenter by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_source_licenses)

        injector.inject(Kodein {
            extend(appKodein())

            import(openSourceLicensesModule(navigator = this@OpenSourceLicensesActivity))
        })

        webView.loadUrl(appConfig.openSourceLicensesUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { presenter.onHomeClicked(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
