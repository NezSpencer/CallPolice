package com.nezspencer.callpolice

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kotlinx.android.synthetic.main.item_phone.view.*
import kotlinx.android.synthetic.main.item_state.view.*

class GroupedContactListAdapter(
    private val context: Context,
    private val contactByStateList: List<ContactByState>,
    private val onPhoneNumberClicked: (String) -> Unit
) : BaseExpandableListAdapter() {
    override fun getChildrenCount(groupId: Int) = contactByStateList[groupId].phones.size

    override fun getGroup(groupId: Int) = contactByStateList[groupId]

    override fun onGroupCollapsed(groupPosition: Int) {
        contactByStateList[groupPosition].isSelected = false
        notifyDataSetChanged()
    }

    override fun onGroupExpanded(groupPosition: Int) {
        super.onGroupExpanded(groupPosition)
        contactByStateList[groupPosition].isSelected = true
        notifyDataSetChanged()
    }

    override fun isEmpty() = contactByStateList.isEmpty()


    override fun getChild(groupId: Int, childId: Int) =
        contactByStateList[groupId].phones[childId]

    override fun getGroupId(p0: Int) = 0L

    override fun isChildSelectable(p0: Int, p1: Int) = true

    override fun hasStableIds() = true

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val phoneNumber = getChild(groupPosition, childPosition)
        val childView: View
        val holder: ChildHolder

        if (convertView == null) {
            childView = LayoutInflater.from(context).inflate(R.layout.item_phone, parent, false)
            holder = ChildHolder(childView)
            childView.tag = holder
        } else {
            childView = convertView
            holder = convertView.tag as ChildHolder
        }
        holder.bind(phoneNumber)
        return childView
    }

    override fun areAllItemsEnabled() = true

    override fun getChildId(p0: Int, p1: Int) = 0L

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val item = getGroup(groupPosition)
        val groupView: View
        val holder: GroupHolder
        if (convertView == null) {
            groupView = LayoutInflater.from(context).inflate(R.layout.item_state, parent, false)
            holder = GroupHolder(groupView)
            groupView.tag = holder
        } else {
            groupView = convertView
            holder = convertView.tag as GroupHolder
        }
        holder.bind(item, item.isSelected)
        return groupView
    }

    override fun getGroupCount() = contactByStateList.size

    fun prepareShareIntentMessage(stateInfo: ContactByState): String {
        val formattedNumberTextBuilder = StringBuilder()
        for (number in stateInfo.phones) {
            formattedNumberTextBuilder.append("$number\n")
        }
        return context.getString(
            R.string.share_number_text,
            stateInfo.state.capitalize(),
            formattedNumberTextBuilder.toString()
        )
    }

    inner class GroupHolder(private val stateView: View) {
        fun bind(stateInfo: ContactByState, isSelected: Boolean) {
            with(stateView) {
                tv_state.text = stateInfo.state.capitalize()
                tv_state.typeface =
                    if (isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                cb_indicator.isChecked = isSelected
                iv_share_numbers.showIf(isSelected)
                tv_state.setTextColor(context.themeColor(if (isSelected) R.attr.colorOnBackground else R.attr.colorOnSurface))
                state_container.setBackgroundColor(context.themeColor(if (isSelected) R.attr.colorSurface else android.R.attr.colorBackground))

                iv_share_numbers.setBlockingClickListener {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, prepareShareIntentMessage(stateInfo))
                    context.startActivity(shareIntent)
                }
            }
        }
    }

    inner class ChildHolder(private val phoneView: View) {
        fun bind(phoneNumber: String) {
            phoneView.tv_phone.text = phoneNumber
            phoneView.setOnClickListener { onPhoneNumberClicked(phoneNumber) }
        }
    }
}