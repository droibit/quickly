package com.droibit.quickly.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.droibit.quickly.BuildConfig
import com.droibit.quickly.R
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.provider.intent.IntentCreator
import com.droibit.quickly.data.repository.appinfo.AppInfo
import com.droibit.quickly.main.MainContract.QuickActionEvent
import com.droibit.quickly.main.MainContract.QuickActionItem
import com.droibit.quickly.main.MainContract.QuickActionItem.*
import com.github.droibit.chopstick.bindView
import com.github.droibit.chopstick.findView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import java.util.*


class QuickActionDialogFragment : BottomSheetDialogFragment(),
        OnItemClickListener {

    companion object {

        @JvmStatic
        fun newInstance(app: AppInfo): QuickActionDialogFragment {
            return QuickActionDialogFragment().apply {
                arguments = Bundle(1).apply { putParcelable(ARG_APP, app) }
            }
        }

        private val ARG_APP = "ARG_APP"
    }

    private val app: AppInfo by lazy { arguments.getParcelable<AppInfo>(ARG_APP) }

    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }
    }

    private val injector = KodeinInjector()

    private val intentCreator: IntentCreator by injector.instance()

    private val rxBus: RxBus by injector.instance()

    private lateinit var listAdapter: QuickActionAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        injector.inject(Kodein {
            val parentKodein = context as? KodeinAware
                    ?: throw IllegalStateException("KodeinAware is not implemented.")
            extend(parentKodein.kodein)
        })
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        listAdapter = QuickActionAdapter(context, items = createQuickActions())

        val contentView = QuickActionLayout(context).apply {
            appName = app.name
            listAdapter = this@QuickActionDialogFragment.listAdapter
            onItemClickListener = this@QuickActionDialogFragment

            showLocked(visible = app.preInstalled)

            dialog.setContentView(this)
        }

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.apply {
                setBottomSheetCallback(bottomSheetCallback)
            }
        }
    }

    // OnItemClickListener

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = listAdapter.getItem(position)
        rxBus.call(item.toEvent(app))

        dismiss()
    }

    // Private

    private fun createQuickActions(): List<QuickActionItem> {
        return ArrayList<QuickActionItem>(3).apply {
            if (BuildConfig.APPLICATION_ID != app.packageName
                    && resolveActivity(intentCreator.newUninstallIntent(app.packageName))) {
                add(UNINSTALL)
            }

            add(SHARE_PACKAGE)

            if (resolveActivity(intentCreator.newAppInfoIntent(app.packageName))) {
                add(OPEN_APP_INFO)
            }
        }
    }

    private fun resolveActivity(intent: Intent): Boolean {
        val resolveInfos = context.packageManager.queryIntentActivities(intent, MATCH_DEFAULT_ONLY)
        return resolveInfos.isNotEmpty()
    }
}

private fun QuickActionItem.toEvent(app: AppInfo): QuickActionEvent {
    return when (this) {
        UNINSTALL -> QuickActionEvent.Uninstall(app)
        SHARE_PACKAGE -> QuickActionEvent.SharePackage(app)
        OPEN_APP_INFO -> QuickActionEvent.OpenAppInfo(app)
    }
}

private class QuickActionLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    var appName: String
        get() = appNameView.text.toString()
        set(value) {
            appNameView.text = value
        }

    var listAdapter: ListAdapter
        get() = listView.adapter
        set(value) {
            listView.adapter = value
        }

    var onItemClickListener: OnItemClickListener
        get() = listView.onItemClickListener
        set(value) {
            listView.onItemClickListener = value
        }

    private val appNameView: TextView by bindView(R.id.app_name)

    private val listView: ListView by bindView(R.id.list)

    private val lockedView: View by bindView(R.id.lock_icon)

    init {
        View.inflate(context, R.layout.fragment_dialog_quick_action, this)
    }

    fun showLocked(visible: Boolean) {
        lockedView.visibility = if (visible) View.VISIBLE else View.GONE
    }
}

// TODO: Hide uninstall item if preinstall app
private class QuickActionAdapter(context: Context, items: List<QuickActionItem>)
    : ArrayAdapter<QuickActionItem>(context, -1, items) {

    private val inflater = LayoutInflater.from(context)

    private class ViewHolder(itemView: View) {
        val iconView: ImageView = itemView.findView(R.id.icon)

        val labelView: TextView = itemView.findView(R.id.label)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.list_item_quick_action, parent, false).apply {
            tag = ViewHolder(itemView = this)
        }
        return view.apply {
            val holder = tag as ViewHolder
            val item = getItem(position)
            holder.labelView.setText(item.labelRes)
            holder.iconView.setImageResource(item.iconRes)
        }
    }
}

