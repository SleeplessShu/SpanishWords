package com.example.spanishwords.score.presentation

import android.os.Bundle
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
        setupObservers()
        setupUI()
    }

    private fun setupUI(){
        binding.scoreRecycleView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers(){
        binding.scoreRecycleView.adapter = ScoreAdapter(scoresList)
    }

   private companion object{
       private val scoresList = listOf(
           GameResult("Alex", "999999999"),
           GameResult("Brian", "888888888"),
           GameResult("Catherine", "777777777"),
           GameResult("Daniel", "666666666"),
           GameResult("Eva", "555555555"),
           GameResult("Frank", "444444444"),
           GameResult("Grace", "333333333"),
           GameResult("Hannah", "222222222")
       )
   }
}