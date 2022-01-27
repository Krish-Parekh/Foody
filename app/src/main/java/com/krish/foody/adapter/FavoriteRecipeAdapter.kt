package com.krish.foody.adapter


import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.krish.foody.R
import com.krish.foody.data.database.entities.FavoritesEntity
import com.krish.foody.models.Result
import com.krish.foody.ui.fragments.favoriteRecipes.FavoriteRecipesFragment
import com.krish.foody.util.RecipesDiffUtil
import com.krish.foody.viewmodel.MainViewModel
import org.jsoup.Jsoup

interface BtnClick {
    fun onRowClick(result: Result)
}

class FavoriteRecipeAdapter(
    private val context: Context,
    private val listener: FavoriteRecipesFragment,
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) :
    RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteRecipeViewHolder>(), ActionMode.Callback {

    private var favoriteRecipe = emptyList<FavoritesEntity>()
    private var multiSelection = false
    private var myViewHolders = arrayListOf<FavoriteRecipeViewHolder>()
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private lateinit var mActionMode: ActionMode
    private lateinit var rootView : View

    inner class FavoriteRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.favorite_recipe_imageView)
        val titleText: TextView = itemView.findViewById(R.id.favorite_title_textView)
        val tvDescription: TextView = itemView.findViewById(R.id.favorite_description_textView)
        val tvHeart: TextView = itemView.findViewById(R.id.favorite_heart_textView)
        val tvClock: TextView = itemView.findViewById(R.id.favorite_clock_textView)
        val leafImageView: ImageView = itemView.findViewById(R.id.favorite_leaf_imageView)
        val tvLeafVegan: TextView = itemView.findViewById(R.id.favorite_leaf_textView)
        val recipeRowLayout: ConstraintLayout = itemView.findViewById(R.id.favoriteRecipesRowLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_recipes_row_layout, parent, false)
        return FavoriteRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView
        holder.apply {
            val currentRecipe = favoriteRecipe[position].result
            titleText.text = currentRecipe.title
            tvDescription.text = Jsoup.parse(currentRecipe.summary).text()
            tvHeart.text = currentRecipe.aggregateLikes.toString()
            tvClock.text = currentRecipe.readyInMinutes.toString()

            if (currentRecipe.vegan) {
                tvLeafVegan.setTextColor(ContextCompat.getColor(context, R.color.green))
                leafImageView.setColorFilter(ContextCompat.getColor(context, R.color.green))
            }
            recipeImage.load(currentRecipe.image) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }


            recipeRowLayout.setOnClickListener {
                if (multiSelection) {
                    applySelection(holder, favoriteRecipe[position])
                } else {
                    listener.onRowClick(currentRecipe)
                }
            }


            recipeRowLayout.setOnLongClickListener {
                if (!multiSelection) {
                    multiSelection = true
                    requireActivity.startActionMode(this@FavoriteRecipeAdapter)
                    applySelection(holder, favoriteRecipe[position])
                    true
                } else {
                    multiSelection = false
                    false
                }
            }
        }
    }

    private fun applySelection(holder: FavoriteRecipeViewHolder, currentRecipe: FavoritesEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(
        holder: FavoriteRecipeViewHolder,
        backgroundColor: Int,
        strokeColor: Int
    ) {
        holder.itemView.apply {
            findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout)
                .setBackgroundColor(ContextCompat.getColor(requireActivity, backgroundColor))
            findViewById<MaterialCardView>(R.id.favorite_row_cardView)
                .strokeColor = ContextCompat.getColor(requireActivity, strokeColor)
        }

    }

    private fun applyActionModeTitle(){
        when(selectedRecipes.size){
             0 -> {
                 mActionMode.finish()
             }
            1 ->{
                mActionMode.title = "${selectedRecipes.size} item selected"
            }
            else ->{
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun getItemCount(): Int = favoriteRecipe.size

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favorite_contextual_menu, menu)
        mActionMode = mode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_favorite_recipe){
            selectedRecipes.forEach{
                mainViewModel.deleteFavoriteRecipes(it)
            }
            showSnackBar("${selectedRecipes.size} Recipes removed")
            selectedRecipes.clear()
            mActionMode.finish()
            multiSelection = false
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }

        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    fun setData(newData: List<FavoritesEntity>) {
        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipe, newData)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipe = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun showSnackBar(message : String){
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}.show()
    }

    fun clearContextualActionMode(){
        if(this::mActionMode.isInitialized){
            mActionMode.finish()
        }
    }
}