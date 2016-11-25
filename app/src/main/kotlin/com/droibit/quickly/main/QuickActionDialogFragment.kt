package com.droibit.quickly.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.droibit.quickly.R
import com.droibit.quickly.data.repository.appinfo.AppInfo


class QuickActionDialogFragment : BottomSheetDialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(app: AppInfo): QuickActionDialogFragment {
            return QuickActionDialogFragment().apply {
                arguments = Bundle(1).apply { putParcelable(ARG_APP, app) }
            }
        }

        private val ARG_APP = "ARG_APP"
    }

    internal class QuickActionLayout @JvmOverloads constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

        init {
            View.inflate(context, R.layout.fragment_dialog_quick_action, this)
        }
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

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = QuickActionLayout(context).apply {
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
}