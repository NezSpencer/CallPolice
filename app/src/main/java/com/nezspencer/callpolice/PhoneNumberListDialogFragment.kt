package com.nezspencer.callpolice

import android.app.Dialog
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_phone_number_list.view.*

class PhoneNumberListDialogFragment(
    private val phoneNumbers: List<MainFragment.StateContact>,
    private val numberSelectedListener: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutView =
            LayoutInflater.from(activity!!).inflate(R.layout.dialog_phone_number_list, null, false)
        dialog.setContentView(layoutView)
        layoutView.rv_state_contact.adapter = StateContactsAdapter(phoneNumbers) {
            dismiss()
            numberSelectedListener(it)
        }.apply { notifyDataSetChanged() }
        layoutView.rv_state_contact.addItemDecoration(
            DividerItemDecoration(
                activity!!,
                LinearLayoutManager.VERTICAL
            )
        )
        val point = Point()
        dialog.window!!.windowManager.defaultDisplay.getSize(point)
        dialog.window?.setLayout((point.x * .5).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        return dialog
    }

    companion object {
        fun display(
            manager: FragmentManager,
            phoneNumbers: List<MainFragment.StateContact>,
            numberSelectedListener: (String) -> Unit
        ) {
            PhoneNumberListDialogFragment(phoneNumbers, numberSelectedListener).show(
                manager,
                PhoneNumberListDialogFragment::javaClass.name
            )
        }
    }
}