package com.droibit.quickly.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.droibit.quickly.R
import com.github.droibit.chopstick.bindView

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.list)

    private val progressBar: ProgressBar by bindView(R.id.progress)

    private val emptyView: View by bindView(R.id.empty)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
