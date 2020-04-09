package com.example.user.bartender.recipes.simple

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.user.bartender.database.BartenderDatabaseDao
import com.example.user.bartender.database.Connection
import com.example.user.bartender.database.Ingredient
import com.example.user.bartender.database.Recipe
import kotlinx.coroutines.*

class RecipesViewModel(val database: BartenderDatabaseDao,
                       application: Application): AndroidViewModel(application){
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)

    val ingredients = database.getAllIngredients()
    val recipes = database.getRecipes("simple")

    private val  _editRecipe = MutableLiveData<Recipe>()
            val editRecipe: LiveData<Recipe> = _editRecipe
    init {
        _editRecipe.value = null
    }

    fun getNames(list: List<Ingredient>?): Array<kotlin.String?> {
        val array = arrayOfNulls<kotlin.String>(list!!.size+1)
        array[0] = "Пусто"
        for (i in 1 until list.size+1)
            array[i] = list[i-1].name
        return array
    }

    fun onItemClick(recipe: Recipe) {
        _editRecipe.value = recipe
    }

    fun onSaveButtonClick(recipe:Recipe) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onSaveButtonClickToEdit(recipeID: Long, toString: String, newIngredients: Array<Triple<String, Double, Int>>) {

    }

    fun onSaveButtonClickToAdd(name: String, ingredients: ArrayList<Triple<String, Double, Int>>) {
        val type = "simple"
        uiScope.launch {
            val newRecipe = Recipe(name = name,type = type)
            insertName(newRecipe)
            insert(newRecipe.recipeId, ingredients)
        }
    }

    private suspend fun insertName(recipe: Recipe){
        withContext(Dispatchers.IO){
            database.insertRecipeName(recipe)
        }
    }

    private suspend fun insert(id: Long ,ingredients: ArrayList<Triple<String, Double, Int>>){
        val list = ArrayList<Connection>()
        withContext(Dispatchers.IO){
            for (i in ingredients.indices) {
                    val connection = Connection(recID = id, ingID = getIngredient(ingredients[i].first), volume = ingredients[i].second, layer = ingredients[i].third)
                    list.add(connection)
            }
            database.insertRecipe(connections = list)
        }
    }


    private fun getIngredient(first: String): Long {
        var id:Long = 0
        uiScope.launch {
            id = withContext(Dispatchers.IO){
                val ingredientId = database.getIngredientId(first).ingredientId
                ingredientId
            }
        }
        return id
    }

    private fun getRecipeType(ingredients: ArrayList<Triple<String, Double, Int>>): String {
        return "simple"
    }

    fun delete(recipe: Recipe) {
        uiScope.launch {
            withContext(Dispatchers.IO){
                database.deleteRecipe(recipe)
                database.deleteConnection(recipe.recipeId.toString())
            }
        }

    }

    fun getIngredients(id: Long): ArrayList<Triple<String, Double, Int>> {
        val ingredients = ArrayList<String>()
        val volumes = ArrayList<Double>()
        val layers = ArrayList<Int>()
        val array = ArrayList<Triple<String, Double, Int>>()
        /*val connections = getConnections(id)
        for(i in connections.indices){
            array.add(Triple(getIngredientByID(connections[i].ingID),connections[i].volume,connections[i].layer))
        }*/
        return array
    }

/*
    private fun getConnections(id: Long): List<Connection>{
        var connections = List<Connection>()
         uiScope.launch {
             connections  = withContext(Dispatchers.IO){
                val list = database.getConnections(id)
                list.value
            }
        }
        return connections
    }
*/

    private fun getIngredientByID(id:Long): String {
        var name:String = ""
        uiScope.launch {
            name = withContext(Dispatchers.IO){
               val name = database.getIngredient(id).name
                name
            }
        }
        return name
    }


}