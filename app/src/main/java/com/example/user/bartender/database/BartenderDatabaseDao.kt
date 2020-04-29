package com.example.user.bartender.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BartenderDatabaseDao{

    @Insert
    fun insertIngredient(ingredient:Ingredient): Long
    @Insert
    fun insertRecipe(recipe: Recipe): Long
    @Insert
    fun insertConnection(connection: Connection)

    @Update
    fun updateIngredient (ingredient: Ingredient): Int
    @Update
    fun updateRecipeName (recipe: Recipe)
    @Update
    fun updateConnection (connection: Connection)

    @Delete
    fun deleteIngredient(ingredient: Ingredient)
    @Delete
    fun deleteRecipe(recipe: Recipe)
    @Query("DELETE FROM connection_table WHERE recipe_id = :key")
    fun deleteConnection(key: String)


    @Query("SELECT * FROM ingredient_table ORDER BY ingredient_name ")
    fun getAllIngredients(): LiveData<List<Ingredient>>
    @Query("SELECT * FROM ingredient_table WHERE ingredient_name = :name ")
    fun getIngredient(name: String): LiveData<List<Ingredient>>

    @Query("SELECT * FROM recipe_table ORDER BY recipe_name")
    fun getRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE recipe_type = :key ORDER BY recipe_name")
    fun filterRecipes(key: String): LiveData<List<Recipe>>

    @Query ("SELECT * FROM connection_table")
    fun getConnections(): LiveData<List<Connection>>

    @Query ("SELECT * FROM connection_table WHERE recipe_id = :id")
    fun getConnectionsByRecipeID(id: Long): LiveData<List<Connection>>
}
