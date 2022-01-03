package com.krish.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipesEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(RecipesTypeConvertor::class)
abstract class RecipesDatabase : RoomDatabase(){

    abstract fun recipesDao() : RecipesDao


}