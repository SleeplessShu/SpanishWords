package com.example.spanishwords.score.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spanishwords.databinding.ScoreFragmentBinding
import com.example.spanishwords.score.models.GameResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScoreFragment : Fragment() {
    private val viewModel: ScoreViewModel by viewModel()
    private var _binding: ScoreFragmentBinding? = null
    private val binding: ScoreFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = ScoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()

    }

    private fun setupUI(){
        binding.scoreRecycleView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers(){
        viewModel.scoreResults.observe(viewLifecycleOwner) { newData ->
            Log.d("DEBUG", "SCOREDATA: ${newData}")
            binding.scoreRecycleView.adapter = ScoreAdapter(newData)
        }

    }

}