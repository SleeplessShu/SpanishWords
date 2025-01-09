package com.example.spanishwords.score.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spanishwords.R

class ViewHolderScore(view: View): RecyclerView.ViewHolder(view) {
    val name: TextView = view.findViewById(R.id.tvName)
    val score: TextView = view.findViewById(R.id.tvScore)
}