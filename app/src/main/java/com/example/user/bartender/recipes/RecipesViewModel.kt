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

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //список всех ингредиентов в системе
    val ingredients = database.getAllIngredients()
    //список всех рецептов
    var recipes = database.getRecipes()
    val connections = database.getConnections()

    //фильтры для отображения рецептов разых типов
    private var showSimpleRecipe: Boolean = true
    private var showLayerRecipe: Boolean = true
    //ID кнопки для определения возможности приготовления рецепта
    private val _buttonID = MutableLiveData<Long>()
    val buttonID: LiveData<Long> = _buttonID
    //статус подключения у роботу и процесса приготовления рецепта
    private val _connectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String> = _connectionStatus
    //состояние кнопки - автивна или нет
    var buttonState = false

    private val smartBartender:SmartBartender = application as SmartBartender

    init {
        _buttonID.value = -1
        _connectionStatus.value = ""
    }

    fun onCookButtonClick(recipe: Recipe) {
        val adapter = smartBartender.btAdapter
        adapter.enable()
        if(adapter.isEnabled){
            val recipeIngredients = getIngredientArrayList(recipe.recipeId, connections.value!!)
            val settings = smartBartender.getMotors()
            val output = generateRecipeString(recipeIngredients, settings)
            val controller = BluetoothController(adapter, output)
            if (controller.socket!= null) {
                //TODO: поправить проверку на подключение
                _connectionStatus.value = "Приготовление напитка началось"
            } else _connectionStatus.value = "Что-то пошло не так! Проверьте подключение"
        } else _connectionStatus.value = "Для приготовления необходимо включить Bluetooth"
        _connectionStatus.value = ""
    }
    //генерация строки рецепта, которая передаётся роботу
    private fun generateRecipeString(recipeIngredients: ArrayList<Triple<Ingredient, Double, Int>>,
                                     motors: ArrayList<Ingredient>): String {
        var output = ""
        recipeIngredients.sortBy { it.third }
        for (i in recipeIngredients.indices) {
            val index = motors.indexOf(recipeIngredients[i].first)
            val engineTicks = (motors[index].c * recipeIngredients[i].second).toInt()
            if (engineTicks != 0) {
                output += "$index,$engineTicks"
            }
            output += if (i + 1 < recipeIngredients.size) {
                if (recipeIngredients[i].third == recipeIngredients[i + 1].third) ":" else ";"
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
            val ingredient = getIngredient(connections[i].ingName)!!
            if (connections[i].recID == id) array.add(Triple(ingredient, connections[i].volume, connections[i].layer))
        }
        return array
    }

    //фильтр рецептов для отображения
    fun filter(){
        return if (showSimpleRecipe && showLayerRecipe) recipes = database.getRecipes()
        else if (showSimpleRecipe && !showLayerRecipe) recipes = database.filterRecipes("simple")
        else if (!showSimpleRecipe && showLayerRecipe) recipes = database.filterRecipes("layer")
        else {
            val ld = MutableLiveData<List<Recipe>>()
            ld.value = emptyList()
            recipes = ld
        }
    }
    //изменение значений флагов для фильтрации рецептов
    fun changeFilterState(i:Int) {
        if(i==0) showSimpleRecipe = !showSimpleRecipe
        else showLayerRecipe = !showLayerRecipe
    }
    //получение состояния кнопки для рецепта по ID
    fun getButtonState(id: Long): Boolean{
        _buttonID.value = id
        return buttonState
    }
    fun isReadyToCook(ingredients: ArrayList<Triple<Ingredient, Double, Int>>) {
            val setting = smartBartender.getMotors()
            for (i in ingredients.indices) {
                if (setting.indexOf(ingredients[i].first) == -1) {
                    buttonState = false
                    return
                }
            }
            buttonState = true
    }
    //получение ингредиента по его имени
    private fun getIngredient(ingredientName: String): Ingredient? {
        val ingredientsList = ingredients.value!!
        for(i in ingredientsList.indices){
            if(ingredientsList[i].name==ingredientName) return ingredientsList[i]
        }
        return null
    }
}

