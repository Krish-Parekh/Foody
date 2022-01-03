package com.krish.foody.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.krish.foody.R
import com.krish.foody.models.FoodRecipe
import com.krish.foody.models.Result
import com.krish.foody.util.RecipesDiffUtil

class RecipesAdapter(private val context : Context) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private var recipes = emptyList<Result>()

    inner class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipe_imageView)
        val titleText: TextView = itemView.findViewById(R.id.title_textView)
        val tvDescription: TextView = itemView.findViewById(R.id.description_textView)
        val tvHeart: TextView = itemView.findViewById(R.id.heart_textView)
        val tvClock: TextView = itemView.findViewById(R.id.clock_textView)
        val leafImageView: ImageView = itemView.findViewById(R.id.leaf_imageView)
        val tvLeafVegan: TextView = itemView.findViewById(R.id.leaf_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recipes_row_layout, parent, false)
        return RecipesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.apply {
            val currentRecipe = recipes[position]

            titleText.text = currentRecipe.title
            tvDescription.text = currentRecipe.summary
            tvHeart.text = currentRecipe.aggregateLikes.toString()
            tvClock.text = currentRecipe.readyInMinutes.toString()

            if (currentRecipe.vegan){
                tvLeafVegan.setTextColor(ContextCompat.getColor(context, R.color.green))
                leafImageView.setColorFilter(ContextCompat.getColor(context, R.color.green))
            }
            recipeImage.load(currentRecipe.image){
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }

        }
    }

    override fun getItemCount(): Int = recipes.size

    fun setData(newData: FoodRecipe) {
        val recipesDiffUtil = RecipesDiffUtil(recipes , newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}