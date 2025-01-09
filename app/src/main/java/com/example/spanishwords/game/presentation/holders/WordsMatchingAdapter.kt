package com.example.spanishwords.game.presentation.holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spanishwords.R
import com.example.spanishwords.game.presentation.models.Word

class WordsMatchingAdapter(
    private var wordsPairs: List<Pair<Word, Word>> = emptyList(),
    private var selectedWords: List<Word> = emptyList(),
    private var errorWords: List<Word> = emptyList(),
    private var usedWords: List<Word> = emptyList(),
    private var correctWords: List<Word> = emptyList(),

    private val onWordClick: (Word) -> Unit,

    private val context: Context

) : RecyclerView.Adapter<ViewHolderWordsMatching>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWordsMatching {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.words_pair, parent, false)
        return ViewHolderWordsMatching(binding)

    }


    override fun onBindViewHolder(holder: ViewHolderWordsMatching, position: Int) {


        val (origin, translate) = wordsPairs[position]
        holder.origin.text = origin.text
        holder.origin.setBackgroundResource(
            getBackground(origin)
        )
        holder.origin.setTextColor(
            getTextColor(origin)
        )
        holder.origin.isEnabled = origin !in usedWords
        holder.origin.setOnClickListener { onWordClick(origin) }
        holder.translate.text = translate.text
        holder.translate.setBackgroundResource(
            getBackground(translate)
        )
        holder.translate.setTextColor(
            getTextColor(translate)
        )
        holder.translate.isEnabled = translate !in usedWords
        holder.translate.setOnClickListener { onWordClick(translate) }

    }


    override fun getItemCount(): Int = wordsPairs.size

    private fun getBackground(word: Word): Int {
        return when (word) {
            in selectedWords -> R.drawable.word_background_selected
            in errorWords -> R.drawable.word_background_error
            in usedWords -> R.drawable.word_background_opaque
            in correctWords -> R.drawable.word_background_correct
            else -> R.drawable.word_background_default
        }
    }

    private fun getTextColor(word: Word): Int {
        return when (word) {
            in usedWords -> ContextCompat.getColor(context, R.color.black_0_25)
            else -> ContextCompat.getColor(context, R.color.white)
        }
    }

    // Обновление списка слов
    fun updateWordsList(newWordsPairs: List<Pair<Word, Word>>) {
        wordsPairs = newWordsPairs
        notifyDataSetChanged()
    }

    // Обновление выделенных слов
    fun updateSelectedWords(newSelectedWords: List<Word>) {
        selectedWords = newSelectedWords
        notifyDataSetChanged()
    }

    // Обновление слов с ошибками
    fun updateErrorWords(newErrorWords: List<Word>) {
        errorWords = newErrorWords
        notifyDataSetChanged()
    }

    fun updateCorrectWords(newCorrectWords: List<Word>) {
        correctWords = newCorrectWords
        notifyDataSetChanged()
    }

    fun updateUsedWords(newUsedWords: List<Word>) {
        usedWords = newUsedWords
        notifyDataSetChanged()
    }
}

