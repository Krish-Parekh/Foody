package com.krish.foody.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.krish.foody.models.FoodRecipe
import java.util.*

class RecipesTypeConvertor {

    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe) : String{
        return gson.toJson(foodRecipe)
    }
    @TypeConverter
    fun stringToFoodRecipe(data : String) : FoodRecipe{
        val listType  = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data , listType)
    }

}