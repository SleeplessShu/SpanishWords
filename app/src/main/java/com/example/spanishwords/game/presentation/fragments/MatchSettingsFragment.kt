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
        parentViewModel.selectedLanguage1.observe(viewLifecycleOwner, Observer { language ->
            binding.tvFirstLanguage.text = language.name
        })

        parentViewModel.selectedLanguage2.observe(viewLifecycleOwner, Observer { language ->
            binding.tvSecondLanguage.text = language.name
        })

        parentViewModel.selectedLanguageLevel.observe(viewLifecycleOwner, Observer { level ->
            binding.tvWordsLevel.text = level.name
        })

        parentViewModel.selectedDifficult.observe(viewLifecycleOwner, Observer { difficult ->
            binding.tvDifficult.text = difficult.name
        })

        parentViewModel.selectedCategory.observe(viewLifecycleOwner, Observer { category ->
            binding.tvWordsCategory.text = category.name
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
            parentViewModel.switchWordsCathegory(isNext = false)
        }
        binding.brWordsCategory.setOnClickListener {
            parentViewModel.switchWordsCathegory(isNext = true)
        }



    }
}
