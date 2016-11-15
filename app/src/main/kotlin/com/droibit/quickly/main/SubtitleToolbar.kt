package com.droibit.quickly.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.droibit.quickly.R


class SubtitleToolbar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.activity_main_subtitle, this)
    }
}