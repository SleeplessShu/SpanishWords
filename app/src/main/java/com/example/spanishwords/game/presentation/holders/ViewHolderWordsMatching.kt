package com.example.spanishwords.game.presentation.holders

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.spanishwords.R

class ViewHolderWordsMatching(view: View) : RecyclerView.ViewHolder(view) {
    val origin: Button = view.findViewById(R.id.bOrigin)
    val translate: Button = view.findViewById(R.id.bTranslate)
}