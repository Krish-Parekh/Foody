package com.krish.foody.ui.fragments.foodJoke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.krish.foody.R
import com.krish.foody.databinding.FragmentFoodJokeBinding
import com.krish.foody.models.FoodJoke
import com.krish.foody.util.Constants.Companion.API_KEY
import com.krish.foody.util.NetworkResult
import com.krish.foody.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "FoodJokeFragment"

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {
    private lateinit var binding: FragmentFoodJokeBinding
    private val mainViewModel by viewModels<MainViewModel>()
    private var foodJoke = "No Food Joke"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentFoodJokeBinding.inflate(inflater, container, false)

        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.foodJokeErrorTextView.visibility = View.INVISIBLE
                    binding.foodJokeTextView.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.foodJokeCardView.visibility = View.VISIBLE
                    binding.foodJokeTextView.text = response.data?.text
                    foodJoke = response.data?.text.toString()
                }
                is NetworkResult.Error -> {
                    loadDataFromCache(response)
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.foodJokeCardView.visibility = View.VISIBLE


                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    binding.foodJokeErrorTextView.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.foodJokeCardView.visibility = View.INVISIBLE
                    Log.d(TAG, "Loading...")
                }
            }
        })

        return binding.root
    }

    private fun loadDataFromCache(response: NetworkResult.Error<FoodJoke>) {
        lifecycleScope.launch {
            mainViewModel.readFoodJoke.observe(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty() && database != null) {
                    binding.foodJokeTextView.text = database[0].foodJoke.text
                    foodJoke = database[0].foodJoke.text
                } else {
                    binding.foodJokeErrorTextView.visibility = View.VISIBLE
                    binding.foodJokeErrorTextView.text = response.message.toString()
                    binding.foodJokeErrorImageView.visibility = View.VISIBLE
                    binding.foodJokeCardView.visibility = View.INVISIBLE
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_food_joke_menu){
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT,foodJoke)
                this.type = "text/plain"
            }
            startActivity(shareIntent)
        }
        return super.onOptionsItemSelected(item)
    }


}