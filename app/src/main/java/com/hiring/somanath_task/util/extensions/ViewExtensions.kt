package com.hiring.somanath_task.util.extensions

import android.view.View
fun View.setOnSingleClickListener(debounceTime: Long = 500L, action: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > debounceTime) {
                lastClickTime = currentTime
                action(v)
            }
        }
    })
}