package com.droibit.quickly.data.provider.date

import android.content.Context
import android.text.format.DateFormat
import com.droibit.quickly.R
import java.util.*


class DateFormatter(private val context: Context) {

    private val dateFormatter = DateFormat.getMediumDateFormat(context)

    private val timeFormatter = DateFormat.getTimeFormat(context)

    fun format(timeMillis: Long): String {
        val date = Date(timeMillis)
        return context.getString(R.string.app_date_format,
                dateFormatter.format(date), timeFormatter.format(date))
    }
}