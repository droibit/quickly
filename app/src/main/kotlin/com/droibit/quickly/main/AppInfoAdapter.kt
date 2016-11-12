package com.droibit.quickly.main

import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.droibit.quickly.R
import com.droibit.quickly.data.provider.date.DateFormatter
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.squareup.picasso.Picasso
import java.util.*

class AppInfoAdapter(
        private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    private val appInfoList: MutableList<AppInfo> = ArrayList()

    override fun getItemCount(): Int {
        return appInfoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appInfoList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.recycler_item_app_info, parent, false)
        return ViewHolder(itemView)
    }

    fun addAll(appInfoList: List<AppInfo>) {
        this.appInfoList.clear()
        this.appInfoList.addAll(appInfoList)
        this.notifyDataSetChanged()
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val iconView: ImageView by bindView(R.id.app_icon)

    private val nameView: TextView by bindView(R.id.app_name)

    private val versionView: TextView by bindView(R.id.app_version)

    private val packageView: TextView by bindView(R.id.app_package)

    private val lastUpdateTimeView: TextView by bindView(R.id.app_last_update_time)

    private val injector = KodeinInjector()

    private val picasso: Picasso by injector.instance()

    private val dateFormatter: DateFormatter by injector.instance()

    init {
        injector.inject(Kodein {
            extend(itemView.context.appKodein())
        })
    }

    fun bind(appInfo: AppInfo) {
        picasso.load(appInfo.iconUri)
                .error(R.mipmap.ic_launcher)
                .into(iconView)

        nameView.text = appInfo.name
        versionView.text = appInfo.version
        packageView.text = appInfo.packageName
        lastUpdateTimeView.text = dateFormatter.format(timeMillis = appInfo.lastUpdateTime)
    }
}

private val AppInfo.iconUri: Uri
    get() = Uri.parse("$SCHEME_ANDROID_RESOURCE://$packageName/$icon")

private val AppInfo.version: String
    get() = "v$versionName ($versionCode)"