package com.krish.foody.ui.fragments.ingredient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.krish.foody.databinding.FragmentIngredientsBinding


class IngredientsFragment : Fragment() {

    private lateinit var binding: FragmentIngredientsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }
}