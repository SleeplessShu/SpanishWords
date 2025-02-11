package com.example.spanishwords.score.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spanishwords.R
import com.example.spanishwords.score.models.GameResult

class ScoreAdapter(private var items: MutableList<GameResult>) : RecyclerView.Adapter<ViewHolderScore>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderScore {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.score_element, parent, false)
        return ViewHolderScore(view)
    }

    override fun onBindViewHolder(holder: ViewHolderScore, position: Int) {
        val item = items[position]
        holder.name.text = item.date
        holder.score.text = item.score
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<GameResult>) {
        items.clear()
        items.addAll(newItems)
        // egusev Android studio suggests, quote:
        //   It will always be more efficient to use more specific change events if you can.
        //    Rely on notifyDataSetChanged as a last resort.
        // I'd trust that - should check
        notifyDataSetChanged()
    }
}




