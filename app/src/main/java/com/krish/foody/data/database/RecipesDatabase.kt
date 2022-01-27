package com.krish.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krish.foody.data.database.entities.FavoritesEntity
import com.krish.foody.data.database.entities.FoodJokeEntity
import com.krish.foody.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class,FavoritesEntity::class,FoodJokeEntity::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(RecipesTypeConvertor::class)
abstract class RecipesDatabase : RoomDatabase(){

    abstract fun recipesDao() : RecipesDao


}