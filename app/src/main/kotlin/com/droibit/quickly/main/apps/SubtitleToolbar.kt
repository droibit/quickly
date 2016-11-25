package com.droibit.quickly.main.apps

import android.content.Context
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.github.droibit.chopstick.bindView


class SubtitleToolbar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val appCountView: TextView by bindView(R.id.app_count)

    private val sortByView: TextView by bindView(R.id.sort_by_label)

    private val orderArrowView: ImageView by bindView(R.id.order_icon)

    init {
        View.inflate(context, R.layout.activity_main_subtitle, this)
    }

    var appCount: Int = 0
        set(value) {
            if (value > 0) {
                appCountView.text = context.getString(R.string.main_subtitle_app_count_format, value)
            }
        }

    private var sortBy = SortBy.NAME

    private var order = Order.ASC

    @UiThread
    fun sortBy(sortBy: SortBy, order: Order) {
        this.sortBy = sortBy
        this.order = order

        this.sortByView.setText(sortBy.stringRes)
        this.orderArrowView.setImageResource(order.drawableRes)
    }

    fun sortByClickListener(listener: (View) -> Unit) {
        sortByView.setOnClickListener(listener)
        orderArrowView.setOnClickListener(listener)
    }
}

private val SortBy.stringRes: Int
    get() {
        return when (this) {
            SortBy.NAME -> R.string.sorted_by_name
            SortBy.PACKAGE -> R.string.sorted_by_package
            SortBy.LAST_UPDATED -> R.string.sorted_by_last_updated
        }
    }

private val Order.drawableRes: Int
    get() {
        return when (this) {
            Order.ASC -> R.drawable.ic_arrow_upward
            Order.DESC -> R.drawable.ic_arrow_downward
        }
    }