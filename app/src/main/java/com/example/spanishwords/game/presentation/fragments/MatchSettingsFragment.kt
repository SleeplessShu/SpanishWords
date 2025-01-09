package com.example.spanishwords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        binding.bNewGame.setOnClickListener {
            parentViewModel.onGame()
        }
    }
}
