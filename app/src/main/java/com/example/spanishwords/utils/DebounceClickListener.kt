package com.example.spanishwords.utils

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebounceClickListener(
    private val lifeCycleOwner: LifecycleOwner,
    private val onDebounceClick: (View) -> Unit
) : View.OnClickListener {

    private var canClick = true

    override fun onClick(v: View) {
        if (canClick) {
            canClick = false
            onDebounceClick(v)
            lifeCycleOwner.lifecycleScope.launch {
                delay(DEBOUNCE_DELAY)
                canClick = true
            }
        }
    }

    private companion object {
        const val DEBOUNCE_DELAY = 1000L
    }
}