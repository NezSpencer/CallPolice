package com.nezspencer.callpolice

import android.content.Context
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import java.util.*

fun Context.themeColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

fun debug(block: () -> Unit) {
    if (BuildConfig.DEBUG)
        block()
}

fun View.setBlockingClickListener(block: () -> Unit) {
    setOnClickListener(object : BlockingClickListener() {
        override fun validClick(view: View?) {
            block()
        }
    })
}

fun View.showIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

abstract class BlockingClickListener(private val threshold: Long = 400L) : View.OnClickListener {
    private val clicks = WeakHashMap<View, Long>()
    abstract fun validClick(view: View?)
    override fun onClick(p0: View?) {
        val prevClickTime = clicks[p0]
        val currentClickTime = SystemClock.uptimeMillis()
        clicks[p0] = currentClickTime
        if (prevClickTime != null && currentClickTime - prevClickTime <= threshold) return
        validClick(p0)
    }
}