package com.krish.foody.data

import com.krish.foody.data.database.RecipesDao
import com.krish.foody.data.database.entities.FavoritesEntity
import com.krish.foody.data.database.entities.FoodJokeEntity
import com.krish.foody.data.database.entities.RecipesEntity
import com.krish.foody.models.FoodJoke
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }


    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    fun readFavoriteRecipes():Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity){
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteFavoriteRecipes(favoritesEntity: FavoritesEntity){
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes(){
        recipesDao.deleteAllFavoriteRecipe()
    }

    fun readFoodJoke() : Flow<List<FoodJokeEntity>>{
        return recipesDao.readFoodJoke()
    }

    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity){
        recipesDao.insertFoodJoke(foodJokeEntity)
    }

}