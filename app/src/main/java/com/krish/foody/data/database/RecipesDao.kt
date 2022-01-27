package com.krish.foody.data.database

import androidx.room.*
import com.krish.foody.data.database.entities.FavoritesEntity
import com.krish.foody.data.database.entities.FoodJokeEntity
import com.krish.foody.data.database.entities.RecipesEntity
import com.krish.foody.models.FoodJoke
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {
    // RecipesEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    // Favorite Entity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipe()

    // FoodJoke Entity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    @Query("SELECT * FROM food_joke_table ORDER BY id ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

}