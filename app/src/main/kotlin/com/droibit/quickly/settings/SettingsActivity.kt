package com.droibit.quickly.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.droibit.quickly.R

class SettingsActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.settings)
        if (fragment != null) {
            fragment.onOptionsItemSelected(item)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}