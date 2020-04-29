package com.example.user.bartender.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient_table")
data class Ingredient(
        @PrimaryKey@ColumnInfo(name = "ingredient_name")
        val name: String,
        @ColumnInfo(name = "converter_coefficient")
        var c: Int
)
