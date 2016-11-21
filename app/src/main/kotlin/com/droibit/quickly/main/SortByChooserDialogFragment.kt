package com.droibit.quickly.main

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.droibit.quickly.R
import com.droibit.quickly.data.provider.eventbus.RxBus
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.Order
import com.droibit.quickly.data.repository.settings.ShowSettingsRepository.SortBy
import com.droibit.quickly.main.MainContract.SortByChooseEvent
import com.github.droibit.chopstick.bindStringArray
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import timber.log.Timber

class SortByChooserDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    companion object {

        @JvmStatic
        fun newInstance(sortBy: SortBy): SortByChooserDialogFragment {
            return SortByChooserDialogFragment().apply {
                arguments = Bundle(1).apply { putSerializable(ARG_SORT_BY, sortBy) }
            }
        }

        private val ARG_SORT_BY = "ARG_SORT_BY"
    }

    private val injector = KodeinInjector()

    private val rxBus: RxBus by injector.instance()

    private val sortByItems: Array<String> by bindStringArray(R.array.sort_by_items)

    private lateinit var sortBy: SortBy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sortBy = savedInstanceState?.getSerializable(ARG_SORT_BY) as? SortBy
                ?: arguments.getSerializable(ARG_SORT_BY) as SortBy
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        injector.inject(Kodein {
            val parentKodein = context as? KodeinAware
                    ?: throw IllegalStateException("KodeinAware is not implemented.")
            extend(parentKodein.kodein)
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context).run {
            setSingleChoiceItems(sortByItems, sortByItems.indexOf(sortBy), this@SortByChooserDialogFragment)

            setTitle(R.string.sorted_by_dialog_title_chooser)
            setPositiveButton(R.string.asc, this@SortByChooserDialogFragment)
            setNegativeButton(R.string.desc, this@SortByChooserDialogFragment)

            create()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_SORT_BY, sortBy)
    }

    // OnClickListener

    override fun onClick(dialog: DialogInterface, which: Int) {
        Timber.d("which=$which")

        if (which < 0) {
            // clicked positive/negative button
            rxBus.call(SortByChooseEvent(sortBy, which.toOrder()))
            return
        }
        sortBy = SortBy.from(index = which)
    }

    private fun Array<String>.indexOf(sortBy: SortBy): Int {
        val sortByString = when (sortBy) {
            SortBy.NAME -> getString(R.string.sorted_by_name)
            SortBy.PACKAGE -> getString(R.string.sorted_by_package)
            SortBy.LAST_UPDATED -> getString(R.string.sorted_by_last_updated)
        }
        return indexOfFirst { sortByString == it }
    }
}

private fun Int.toOrder(): Order = if (this == BUTTON_POSITIVE) Order.ASC else Order.DESC
