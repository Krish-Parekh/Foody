package com.krish.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.krish.foody.models.Result
import com.krish.foody.util.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var result: Result
)