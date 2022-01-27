package com.krish.foody.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.krish.foody.data.Repository
import com.krish.foody.data.database.entities.FavoritesEntity
import com.krish.foody.data.database.entities.FoodJokeEntity
import com.krish.foody.data.database.entities.RecipesEntity
import com.krish.foody.models.FoodJoke
import com.krish.foody.models.FoodRecipe
import com.krish.foody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** Room Database */

    val readRecipe: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipe: LiveData<List<FavoritesEntity>> =
        repository.local.readFavoriteRecipes().asLiveData()

    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }
    }

    private fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFoodJoke(foodJokeEntity)
        }

    fun deleteFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipes(favoritesEntity)
        }
    }

    fun deleteAllFavoriteRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }
    }


    /** Retrofit */
    var recipeResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()


    fun getFoodJoke(apiKey : String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }


    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) =
        viewModelScope.launch {
            searchRecipesSafeCall(searchQuery)
        }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipeResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipeResponse.value = handleFoodRecipeResponse(response)
                val foodRecipe = recipeResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e: Exception) {
                recipeResponse.value = NetworkResult.Error("Recipes not found")
            }
        } else {
            recipeResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodJokeResponse(response)

                val foodJoke = foodJokeResponse.value!!.data
                if(foodJoke != null){
                    offlineCacheFoodJoke(foodJoke)
                }
            } catch (e: Exception) {
                foodJokeResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }
    private fun handleFoodRecipeResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }




    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager


        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}