package com.example.spanishwords.score.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spanishwords.R
import com.example.spanishwords.score.models.GameResult

class ScoreAdapter(private val items: List<GameResult>) : RecyclerView.Adapter<ViewHolderScore>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderScore {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.score_element, parent, false)
        return ViewHolderScore(view)
    }

    override fun onBindViewHolder(holder: ViewHolderScore, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.score.text = item.score
    }

    override fun getItemCount(): Int = items.size
    }




