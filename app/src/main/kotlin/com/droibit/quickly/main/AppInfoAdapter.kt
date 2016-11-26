package com.droibit.quickly.main

import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.net.Uri
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.droibit.quickly.R
import com.droibit.quickly.data.provider.date.DateFormatter
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import java.util.*

class AppInfoAdapter(
        private val context: Context) : RecyclerView.Adapter<AppInfoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val iconView: ImageView by bindView(R.id.app_icon)

        private val nameView: TextView by bindView(R.id.app_name)

        private val versionView: TextView by bindView(R.id.app_version)

        private val packageView: TextView by bindView(R.id.app_package)

        private val lastUpdateTimeView: TextView by bindView(R.id.app_last_update_time)

        private val moreView: View by bindView(R.id.more)

        private val injector = KodeinInjector()

        private val dateFormatter: DateFormatter by injector.instance()

        init {
            injector.inject(Kodein {
                extend(itemView.context.appKodein())
            })
        }

        fun bind(appInfo: AppInfo) {
            Glide.with(itemView.context)
                    .load(appInfo.iconUri)
                    .error(R.mipmap.ic_launcher)
                    .into(iconView)

            nameView.text = appInfo.name
            versionView.text = appInfo.version
            packageView.text = appInfo.packageName
            lastUpdateTimeView.text = dateFormatter.format(timeMillis = appInfo.lastUpdateTime)
        }

        fun clickListener(listener: (View) -> Unit) = itemView.setOnClickListener(listener)

        fun moreClickListener(listener: (View) -> Unit) = moreView.setOnClickListener(listener)
    }

    private val sortedItemsCallback = SortedListCallback(this)

    private val rawItems = SortedList(AppInfo::class.java, sortedItemsCallback)

    val isEmpty: Boolean
        get() = rawItems.size() == 0

    val items: List<AppInfo>
        get() {
            return ArrayList<AppInfo>(rawItems.size()).apply {
                (0..rawItems.size()-1).mapTo(this) { rawItems.get(it) }
            }
        }

    var itemClickListener: ((AppInfo) -> Unit)? = null

    var moreItemClickListener: ((AppInfo) -> Unit)? = null

    var comparator: Comparator<AppInfo>
        get() = sortedItemsCallback.comparator
        set(value) { sortedItemsCallback.comparator = value }

    override fun getItemCount(): Int {
        return rawItems.size()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rawItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item_app_info, parent, false)
        return ViewHolder(itemView).apply {
            clickListener {
                itemClickListener?.invoke(rawItems[adapterPosition])
            }
            moreClickListener {
                moreItemClickListener?.invoke(rawItems[adapterPosition])
            }
        }
    }

    fun addAll(newItems: List<AppInfo>) {
        this.rawItems.addAll(newItems)
    }

    fun replaceAll(newItems: List<AppInfo>) {
        this.rawItems.beginBatchedUpdates()
        try {
            this.rawItems.clear()
            this.rawItems.addAll(newItems)
        } finally {
            this.rawItems.endBatchedUpdates()
        }
    }

    fun clear() {
        rawItems.clear()
    }

    fun refresh() {
        replaceAll(newItems = items)
    }
}

private class SortedListCallback(private val adapter: AppInfoAdapter)
    : SortedList.Callback<AppInfo>() {

    var comparator = Comparator<AppInfo> { lhs, rhs -> throw NotImplementedError() }

    override fun compare(o1: AppInfo, o2: AppInfo): Int {
        return comparator.compare(o1, o2)
    }

    override fun areItemsTheSame(item1: AppInfo, item2: AppInfo): Boolean {
        return item1.packageName == item2.packageName
    }

    override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem == newItem
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChanged(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }
}

private val AppInfo.iconUri: Uri
    get() = Uri.parse("$SCHEME_ANDROID_RESOURCE://$packageName/$icon")

private val AppInfo.version: String
    get() = "v$versionName ($versionCode)"