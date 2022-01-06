package com.krish.foody.ui.fragments.ingredient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.foody.adapter.IngredientsAdapter
import com.krish.foody.databinding.FragmentIngredientsBinding
import com.krish.foody.models.Result
import com.krish.foody.util.Constants.Companion.RECIPE_RESULT_KEY

private const val TAG = "IngredientsFragment"
class IngredientsFragment : Fragment() {

    private lateinit var binding: FragmentIngredientsBinding
    private val mAdapter: IngredientsAdapter by lazy {
        IngredientsAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        setupRecyclerView()

        val args = arguments
        val myBundle: Result = args!!.getParcelable<Result>(RECIPE_RESULT_KEY) as Result

        myBundle.extendedIngredients.let {
            mAdapter.setData(it)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.adapter = mAdapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }
}