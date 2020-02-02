package com.nezspencer.callpolice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_user_state_phone.view.*

class StateContactsAdapter(
    private val phoneNumbers: List<MainFragment.StateContact>,
    private val onNumberSelected: (String) -> Unit
) :
    RecyclerView.Adapter<StateContactsAdapter.Holder>() {
    private var lastSelectedItemPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_state_phone,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = phoneNumbers.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(phoneNumbers[position])
    }

    inner class Holder(private val rootView: View) : RecyclerView.ViewHolder(rootView) {
        fun bind(contact: MainFragment.StateContact) {
            with(rootView) {
                radio_phone.text = contact.phoneNumber
                radio_phone.isChecked = contact.isSelected
                radio_phone.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        phoneNumbers[lastSelectedItemPosition].isSelected = false
                        phoneNumbers[adapterPosition].isSelected = true
                        lastSelectedItemPosition = adapterPosition
                        onNumberSelected(contact.phoneNumber)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}