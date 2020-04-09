package com.example.user.bartender.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BartenderDatabaseDao{

    @Insert
    fun insertIngredient(ingredient:Ingredient)

    @Insert
    fun insertRecipeName(recipe: Recipe)

    @Insert
    fun insertRecipe(connections: List<Connection>)

    @Update
    fun updateIngredient (ingredient: Ingredient)

    @Delete
    fun deleteIngredient(ingredient: Ingredient)

    @Delete
    fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM connection_table WHERE recipe_id = :key")
    fun deleteConnection(key: String)

    @Update
    fun updateRecipe (recipe: Recipe, connections: List<Connection>)

    @Query("SELECT * FROM ingredient_table ORDER BY ingredient_name ")
    fun getAllIngredients(): LiveData<List<Ingredient>>

    @Query("SELECT * FROM recipe_table WHERE recipe_type = :key")
    fun getRecipes(key: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM ingredient_table WHERE ingredientId = :id")
    fun getIngredient(id: Long): Ingredient

    @Query("SELECT * FROM ingredient_table WHERE ingredient_name = :name ")
    fun getIngredientId(name: String): Ingredient

    @Query ("SELECT * FROM connection_table WHERE recipe_id = :id")
    fun getConnections(id: Long): LiveData<List<Connection>>
}
