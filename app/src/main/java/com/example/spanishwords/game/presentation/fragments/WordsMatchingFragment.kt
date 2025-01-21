package com.example.spanishwords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spanishwords.App
import com.example.spanishwords.R
import com.example.spanishwords.databinding.WordsMatchingFragmentBinding
import com.example.spanishwords.game.presentation.GameViewModel
import com.example.spanishwords.game.presentation.models.Word
import com.example.spanishwords.game.presentation.holders.WordsMatchingAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WordsMatchingFragment : Fragment(R.layout.words_matching_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(
        owner = { requireParentFragment() }
    )
    private var _binding: WordsMatchingFragmentBinding? = null
    private val binding: WordsMatchingFragmentBinding get() = _binding!!
    private lateinit var adapter: WordsMatchingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = WordsMatchingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        adapter = WordsMatchingAdapter(
            context = App.appContext,
            onWordClick = {word: Word -> parentViewModel.onWordClick(word)}
            )
        binding.rvWordsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWordsList.adapter = adapter
    }

    private fun setupObservers() {
        parentViewModel.wordsPairs.observe(viewLifecycleOwner) { wordsPairs ->
            adapter.updateWordsList(wordsPairs)
        }

        parentViewModel.ingameWordsState.observe(viewLifecycleOwner) { ingameWordState ->
            adapter.updateSelectedWords(ingameWordState.selectedWords)
            adapter.updateErrorWords(ingameWordState.errorWords)
            adapter.updateCorrectWords(ingameWordState.correctWords)
            adapter.updateUsedWords(ingameWordState.usedWords)
        }
        parentViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            binding.tvScores.setText(gameState.score)
            setHearts(gameState.lives)
        }

    }

    private fun setHearts(heartsQuantity: Int) {
        when (heartsQuantity) {
            3 -> {
                binding.heart1.setImageResource(R.drawable.ic_heart_full)
                binding.heart2.setImageResource(R.drawable.ic_heart_full)
                binding.heart3.setImageResource(R.drawable.ic_heart_full)
            }

            2 -> {
                binding.heart1.setImageResource(R.drawable.ic_heart_full)
                binding.heart2.setImageResource(R.drawable.ic_heart_full)
                binding.heart3.setImageResource(R.drawable.ic_heart_empty)
            }

            1 -> {
                binding.heart1.setImageResource(R.drawable.ic_heart_full)
                binding.heart2.setImageResource(R.drawable.ic_heart_empty)
                binding.heart3.setImageResource(R.drawable.ic_heart_empty)
            }

            0 -> {
                binding.heart1.setImageResource(R.drawable.ic_heart_empty)
                binding.heart2.setImageResource(R.drawable.ic_heart_empty)
                binding.heart3.setImageResource(R.drawable.ic_heart_empty)
            }

        }
    }
}