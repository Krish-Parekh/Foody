package com.krish.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.foody.R
import com.krish.foody.adapter.RecipesAdapter
import com.krish.foody.databinding.FragmentRecipesBinding
import com.krish.foody.models.FoodRecipe
import com.krish.foody.util.NetworkResult
import com.krish.foody.util.NetworkResult.*
import com.krish.foody.util.observeOnce
import com.krish.foody.viewmodel.MainViewModel
import com.krish.foody.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "RecipesFragment"

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private val args by navArgs<RecipesFragmentArgs>()

    private lateinit var mViewModel: MainViewModel
    private lateinit var mRecipesViewModel: RecipesViewModel
    private val mRecipeAdapter by lazy {
        RecipesAdapter(requireContext())
    }
    private lateinit var binding: FragmentRecipesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mRecipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        readDatabase()
        setUpRecyclerView()

        mViewModel.recipeResponse.observe(viewLifecycleOwner, Observer { networkResponse ->
            mViewModel.readRecipe.observe(viewLifecycleOwner, Observer { database ->
                if (networkResponse is Error && database.isNullOrEmpty()) {
                    binding.errorImage.visibility = View.VISIBLE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = networkResponse.message.toString()
                } else if (networkResponse is Loading) {
                    binding.errorImage.visibility = View.INVISIBLE
                    binding.errorMessage.visibility = View.INVISIBLE
                } else if (networkResponse is Success) {
                    binding.errorImage.visibility = View.INVISIBLE
                    binding.errorMessage.visibility = View.INVISIBLE
                }
            })
        })

        binding.recipeFab.setOnClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
        }
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.adapter = mRecipeAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mViewModel.readRecipe.observeOnce(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d(TAG, "readDatabase called!")
                    mRecipeAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            })
        }
    }

    private fun requestApiData() {
        Log.d(TAG, "requestApiData called!")
        mViewModel.getRecipes(mRecipesViewModel.applyQueries())
        mViewModel.recipeResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Success -> {
                    hideShimmerEffect()
                    response.data?.let {
                        mRecipeAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Loading -> {
                    showShimmerEffect()
                }
            }
            setResponse(response)
        })
    }

    private fun setResponse(response: NetworkResult<FoodRecipe>?) {
        if (response is NetworkResult.Error && mViewModel.readRecipe.value.isNullOrEmpty()) {
            binding.errorImage.visibility = View.VISIBLE
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = response.message
        } else {
            binding.errorImage.visibility = View.INVISIBLE
            binding.errorMessage.visibility = View.INVISIBLE
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mViewModel.readRecipe.observe(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty()) {
                    mRecipeAdapter.setData(database[0].foodRecipe)
                }
            })
        }
    }


    private fun showShimmerEffect() {
        binding.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerView.hideShimmer()
    }
}