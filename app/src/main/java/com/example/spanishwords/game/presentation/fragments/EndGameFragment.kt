package com.example.spanishwords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spanishwords.R
import com.example.spanishwords.databinding.EndGameFragmentBinding
import com.example.spanishwords.game.presentation.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EndGameFragment : Fragment(R.layout.end_game_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(owner = { requireParentFragment() })
    private var _binding: EndGameFragmentBinding? = null
    private val binding: EndGameFragmentBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = EndGameFragmentBinding.inflate(inflater, container, false)
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


        binding.bNewGame.setOnClickListener {
            parentViewModel.onMatchSettings()
        }
        binding.bRestart.setOnClickListener {
            parentViewModel.restartGame()
        }
    }

    private fun setupObservers() {
        parentViewModel.gameState.observe(viewLifecycleOwner) { state ->
            if (state.lives == 0) {
                binding.tvResult.setText(R.string.end_game_phrase_loose)
            } else {
                binding.tvResult.setText(R.string.end_game_phrase_win)
            }
            binding.tvScore.setText(state.score)
        }
    }
}



