package com.krish.foody.ui.fragments.favoriteRecipes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.krish.foody.R
import com.krish.foody.adapter.BtnClick
import com.krish.foody.adapter.FavoriteRecipeAdapter
import com.krish.foody.databinding.FragmentFavoriteRecipesBinding
import com.krish.foody.models.Result
import com.krish.foody.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment(), BtnClick {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val favoriteAdapter: FavoriteRecipeAdapter by lazy {
        FavoriteRecipeAdapter(
            requireContext(),
            this@FavoriteRecipesFragment,
            requireActivity(),
            mainViewModel
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        setupRecyclerView()

        mainViewModel.readFavoriteRecipe.observe(viewLifecycleOwner, Observer { favoriteEntity ->
            if (favoriteEntity.isEmpty()) {
                binding.noDataImageView.visibility = View.VISIBLE
                binding.noDataTextView.visibility = View.VISIBLE
            } else {
                binding.noDataImageView.visibility = View.INVISIBLE
                binding.noDataTextView.visibility = View.INVISIBLE
            }
            favoriteAdapter.setData(favoriteEntity)
        })


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_recipe_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAll_favorite_recipes_menu){
            mainViewModel.deleteAllFavoriteRecipes()
            showSnackBar()
        }

        return super.onOptionsItemSelected(item)
    }
    private fun showSnackBar(){
        Snackbar.make(
            binding.root,
            "All recipes removed",
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}
            .show()
    }
    private fun setupRecyclerView() {
        binding.favoriteRecipesRecyclerView.adapter = favoriteAdapter
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onRowClick(result: Result) {
        val action =
            FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(result)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        favoriteAdapter.clearContextualActionMode()
    }
}