package com.example.spanishwords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.spanishwords.R
import com.example.spanishwords.databinding.LoadingBinding
import com.example.spanishwords.databinding.MatchSettingsFragmentBinding
import com.example.spanishwords.game.presentation.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoadingFragment : Fragment(R.layout.loading){

    private var _binding: LoadingBinding? = null
    private val binding: LoadingBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = LoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        val ivList = listOf(binding.iv1, binding.iv2, binding.iv3, binding.iv4, binding.iv5, binding.iv6)
        ivList.forEachIndexed { index, imageView ->
            rotateIndefinitely(imageView, index * 200L)
        }
    }

    private fun rotateIndefinitely(iv: ImageView, startDelay: Long) {
        iv.animate()
            .rotationBy(360f)         // Поворачиваем на 360°
            .setDuration(1000)        // Длительность анимации 1 сек
            .setStartDelay(startDelay) // Задержка перед стартом
            .withEndAction {
                // Когда заканчивается один полный оборот, запускаем анимацию снова
                //rotateIndefinitely(iv, 0L)
            }
            .start()
    }
}