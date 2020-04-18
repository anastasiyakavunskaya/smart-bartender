package com.example.user.bartender.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class Recipe(
        @PrimaryKey(autoGenerate = true)
        var recipeId: Long = 0L,
        @ColumnInfo(name = "recipe_name")
        var name: String,
        @ColumnInfo(name = "recipe_type")
        var type: String
)