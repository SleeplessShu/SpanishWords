package com.example.spanishwords.game.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spanishwords.R
import com.example.spanishwords.databinding.GameFragmentBinding
import com.example.spanishwords.game.presentation.models.GameState
import com.example.spanishwords.game.presentation.fragments.EndGameFragment
import com.example.spanishwords.game.presentation.fragments.LoadingFragment
import com.example.spanishwords.game.presentation.fragments.MatchSettingsFragment
import com.example.spanishwords.game.presentation.fragments.TestFragment
import com.example.spanishwords.game.presentation.fragments.WordsMatchingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.log

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by viewModel()
    private var _binding: GameFragmentBinding? = null
    private val binding: GameFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

    }


    private fun setupObservers(){
        viewModel.gameState.observe(viewLifecycleOwner) { newState ->
            Log.d("DEBUG", "setupObservers: ${newState.state} ")
            when (newState.state){

                GameState.MATCH_SETTINGS -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, MatchSettingsFragment())
                        .commit()
                    binding.tvHeader.setText(R.string.state_title_match_settings)
                }

                GameState.LOADING -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, LoadingFragment())
                        .commit()
                    binding.tvHeader.setText(R.string.state_title_loading)
                }

                GameState.GAME -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, WordsMatchingFragment())
                        .commit()
                    binding.tvHeader.setText(R.string.empty)
                }

                GameState.END_OF_GAME -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, EndGameFragment())
                        .commit()
                    binding.tvHeader.setText(R.string.empty)

                }
            }
        }
    }
}