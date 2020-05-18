package com.example.user.bartender.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.user.bartender.SmartBartender
import com.example.user.bartender.bluetooth.BluetoothController
import com.example.user.bartender.database.BartenderDatabaseDao
import com.example.user.bartender.database.Connection
import com.example.user.bartender.database.Ingredient
import com.example.user.bartender.database.Recipe
import kotlinx.coroutines.*


class RecipesViewModel(val database: BartenderDatabaseDao,
                       application: Application): AndroidViewModel(application) {

    private var viewModelJob = SupervisorJob()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    //список всех ингредиентов в системе
    val ingredientsLiveData = database.getAllIngredients()
    //список всех рецептов
    var recipesLiveData = database.getRecipes()
    val connectionsLiveData = database.getConnections()

    val _recipes = MutableLiveData<List<ExtendedRecipe>>()
    val recipes: LiveData<List<ExtendedRecipe>> = _recipes

    private val smartBartender:SmartBartender = application as SmartBartender
    val settings = smartBartender.getMotors()
    var ingredientsList = listOf<Ingredient>()
    var recipesList = listOf<Recipe>()
    var connectionsList = listOf<Connection>()

    //статус подключения у роботу и процесса приготовления рецепта
    private val _connectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String> = _connectionStatus

    init {
        ingredientsLiveData.observeForever { ingredients ->
            ingredientsList = ingredients
            recipesLiveData.observeForever { recipes ->
                recipesList = recipes
                updateExtendedRecipes(recipesList, connectionsList)
            }
            connectionsLiveData.observeForever { connections ->
                connectionsList = connections
                updateExtendedRecipes(recipesList, connectionsList)
            }
        }
    }

   private fun updateExtendedRecipes(recipesList: List<Recipe>, connectionsList: List<Connection>): List<ExtendedRecipe> {
        val extendedRecipesList = mutableListOf<ExtendedRecipe>()
        for(i in recipesList.indices){
            val recipeID = recipesList[i].recipeId
            val ingredientList = mutableListOf<Connection>()
            if(connectionsList.isNotEmpty())
                for(j in connectionsList.indices){
                    val ingredient = connectionsList[j]
                    if(ingredient.recID==recipeID) ingredientList.add(ingredient)
                }
            var isReadyToCook= true
            if(ingredientList.isNotEmpty()) isReadyToCook = isReadyToCook(ingredientList)
            val extendedRecipe = ExtendedRecipe(recipesList[i], isReadyToCook, ingredientList)
            extendedRecipesList.add(extendedRecipe)
        }
        _recipes.value = extendedRecipesList
       return extendedRecipesList
   }
    //проверка можно ли приготовить коктейль
    private fun isReadyToCook(ingredients: List<Connection>): Boolean {
        for (i in ingredients.indices) {
            val ingredient = getIngredient(ingredients[i].ingName)
            if (settings.indexOf(ingredient) == -1) return false
        }
       return true
    }
    //получение ингредиента по его имени
    private fun getIngredient(ingredientName: String): Ingredient? {
        for (i in ingredientsList.indices) {
            if (ingredientsList[i].name == ingredientName) return ingredientsList[i]
        }
        return null
    }

    fun onCookButtonClick(recipe: ExtendedRecipe) {
          val adapter = smartBartender.btAdapter
          adapter.enable()
          if(adapter.isEnabled){
              val settings = smartBartender.getMotors()
              val output = generateRecipeString(recipe, settings)
              val controller = BluetoothController(adapter, output)
              if (controller.socket!= null) {
                  //TODO: поправить проверку на подключение
                  _connectionStatus.value = "Приготовление напитка началось. К роботу отправлена строка: $output"
              } else _connectionStatus.value = "Что-то пошло не так! Проверьте подключение"
          } else _connectionStatus.value = "Для приготовления необходимо включить Bluetooth"
          _connectionStatus.value = ""
    }
    //генерация строки рецепта, которая передаётся роботу
    private fun generateRecipeString(recipe: ExtendedRecipe,
                                     motors: ArrayList<Ingredient>): String {
        var output = ""
        val ingredients = recipe.ingredients as ArrayList
        ingredients.sortBy { it.layer }
        for (i in ingredients.indices) {
            val ingredient = ingredients[i]
            val index = motors.indexOf(getIngredient(ingredient.ingName))
            val engineTicks = (motors[index].c * ingredient.volume).toInt()
            if (engineTicks != 0) {
                output += "$index,$engineTicks"
            }
            output += if (i + 1 < ingredients.size) {
                if (ingredients[i].layer == ingredients[i + 1].layer) ":" else ";"
            } else "."
        }
        return output
    }
    //приведение списка ингредиентов к виду массива строк с названиями ингредиентов для Spinner'ов
    fun convertIngredientListToStringArray(list: List<Ingredient>?): Array<String?> {
        val array = arrayOfNulls<String>(list!!.size + 1)
        array[0] = "Пусто"
        for (i in 1 until list.size + 1)
            array[i] = list[i - 1].name
        return array
    }
    //подготовка данных к изменению списка рецепто или сохранения нового рецепта
    fun onSaveButtonClick(recipeID: Long, name: String, ingredients: ArrayList<Triple<String, Double, Int>>) {
        val type = getRecipeType(ingredients)
        if(recipeID>-1) updateRecipe(recipeID, Recipe(recipeId = recipeID, name = name, type = type), ingredients)
        else addRecipe(Recipe(name = name, type = type),ingredients)
    }
    private fun addRecipe(recipe: Recipe, ingredients: ArrayList<Triple<String, Double, Int>>){
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val id = database.insertRecipe(recipe)
                for (i in ingredients.indices) {
                    val connection = Connection(recID = id, ingName = ingredients[i].first, volume = ingredients[i].second, layer = ingredients[i].third)
                    database.insertConnection(connection)
                }
            }
        }
    }
    private fun updateRecipe(recipeID: Long, recipe: Recipe, ingredients: ArrayList<Triple<String, Double, Int>>){
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.deleteConnection(recipeID.toString())
                database.updateRecipeName(recipe)
                for (i in ingredients.indices) {
                    val connection = Connection(recID = recipeID, ingName = ingredients[i].first, volume = ingredients[i].second, layer = ingredients[i].third)
                    database.insertConnection(connection)
                }
            }
        }
    }
    //определение типа рецепта по количеству слоёв в нем
    private fun getRecipeType(ingredients: ArrayList<Triple<String, Double, Int>>): String {
        val layers = ArrayList<Int>()
        for (i in ingredients.indices)
            layers.add(ingredients[i].third)
        val unique = layers.distinct()
        return if (unique.size == 1) "simple"
        else "layer"
    }
    fun delete(recipe: Recipe) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database.deleteRecipe(recipe)
                    database.deleteConnection(recipe.recipeId.toString())
                }
            }
    }
    //список ингредиентов с информацией о объёмах и слоях
    fun getIngredientArrayList(id: Long, connections: List<Connection>): ArrayList<Triple<Ingredient, Double, Int>> {
        val array = ArrayList<Triple<Ingredient, Double, Int>>()
        for (i in connections.indices) {
            val ingredient = getIngredient(connections[i].ingName)
            if(ingredient!=null) {
                if (connections[i].recID == id) array.add(Triple(ingredient, connections[i].volume, connections[i].layer))
            }
        }
        return array
    }

   //фильтр рецептов для отображения
    fun filter(filterSimple: Boolean, filterLayer: Boolean) {
      val recipeList = updateExtendedRecipes(recipesList,connectionsList)
      val extendedRecipesList = mutableListOf<ExtendedRecipe>()
      for(i in recipeList.indices){
          val recipe = recipeList[i]
          val recipeType = recipe.recipe.type
          if(filterSimple&&recipeType=="simple") extendedRecipesList.add(recipe)
          if(filterLayer&&recipeType=="layer") extendedRecipesList.add(recipe)
      }
      _recipes.value = extendedRecipesList
  }
}

