package com.example.spanishwords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.spanishwords.R
import com.example.spanishwords.databinding.MatchSettingsFragmentBinding
import com.example.spanishwords.game.presentation.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MatchSettingsFragment : Fragment(R.layout.match_settings_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(
        owner = { requireParentFragment() }
    )
    private var _binding: MatchSettingsFragmentBinding? = null
    private val binding: MatchSettingsFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = MatchSettingsFragmentBinding.inflate(inflater, container, false)
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

        // Подписка на изменения LiveData
        parentViewModel.gameSettings.observe(viewLifecycleOwner, Observer { settings ->
            binding.tvFirstLanguage.text = settings.language1.toString()
            binding.tvSecondLanguage.text = settings.language2.toString()
            binding.tvWordsLevel.text = settings.level.toString()
            binding.tvDifficult.text = settings.difficult.toString()
            binding.tvWordsCategory.text = settings.category.toString()
        })

        binding.bNewGame.setOnClickListener {
            parentViewModel.onGame()
        }

        binding.blLanguage1.setOnClickListener{
            parentViewModel.switchLanguage1(isNext = false)
        }
        binding.brLanguage1.setOnClickListener {
            parentViewModel.switchLanguage1(isNext = true)
        }

        binding.blLanguage2.setOnClickListener{
            parentViewModel.switchLanguage2(isNext = false)
        }
        binding.brLanguage2.setOnClickListener {
            parentViewModel.switchLanguage2(isNext = true)
        }

        binding.blWordsLevel.setOnClickListener{
            parentViewModel.switchWordsLevel(isNext = false)
        }
        binding.brWordsLevel.setOnClickListener {
            parentViewModel.switchWordsLevel(isNext = true)
        }

        binding.blDifficult.setOnClickListener{
            parentViewModel.switchDifficultLevel(isNext = false)
        }
        binding.brDifficult.setOnClickListener {
            parentViewModel.switchDifficultLevel(isNext = true)
        }

        binding.blWordsCategory.setOnClickListener{
            parentViewModel.switchWordsCategory(isNext = false)
        }
        binding.brWordsCategory.setOnClickListener {
            parentViewModel.switchWordsCategory(isNext = true)
        }



    }
}
