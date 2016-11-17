package com.droibit.quickly.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.droibit.quickly.R
import com.github.droibit.chopstick.bindView


class SubtitleToolbar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val appCountView: TextView by bindView(R.id.app_count)

    init {
        View.inflate(context, R.layout.activity_main_subtitle, this)
    }

    var appCount: Int = 0
        set(value) {
            if (value > 0) {
                appCountView.text = context.getString(R.string.main_subtitle_app_count_format, value)
            }
        }
}