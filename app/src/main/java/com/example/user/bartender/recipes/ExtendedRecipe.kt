package com.example.user.bartender.recipes

import com.example.user.bartender.database.Connection
import com.example.user.bartender.database.Recipe

data class ExtendedRecipe(
        val recipe: Recipe,
        var isReadyToCook: Boolean,
        val ingredients: List<Connection>
)