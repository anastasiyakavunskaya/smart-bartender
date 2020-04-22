package com.example.user.bartender.recipes

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.user.bartender.SmartBartender
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

    val ingredients = database.getAllIngredients()
    var recipes = database.getRecipes()
    val connections = database.getConnections()

    private val _simpleFilter = MutableLiveData<Boolean>()
    val simpleFilter: LiveData<Boolean> = _simpleFilter
    private val _layerFilter = MutableLiveData<Boolean>()
    val layerFilter: LiveData<Boolean> = _layerFilter
    private val _buttonID = MutableLiveData<Long>()
    val buttonID: LiveData<Long> = _buttonID
    var buttonStatus = false

    private val smartBartender:SmartBartender = application as SmartBartender

    init {
        _simpleFilter.value = true
        _layerFilter.value = true
        _buttonID.value = -1
        //recipes = filter(simpleFilter.value!!,layerFilter.value!!)
    }


    fun onCookButtonClick(recipe: Recipe) {
        Log.d("button", "from View Model Click")
        /*val output: String
        val recipe: IntArray = getRecipe(item)
        val isLayer: Boolean = isLayer(item)
        //TODO переделать генерацию строки для слоёных коктейлей
        //TODO переделать генерацию строки для слоёных коктейлей
        if (isLayer) output = generateSimpleOutputString(recipe) else output = generateSimpleOutputString(recipe)
        if (mSocket != null) {
            mConnectedThread.write(output)
            presenter.showError("Приготовление напитка началось")
        } else {
            Log.d(TAG, "...Не удалось отправить...")
            presenter.showError("Что-то пошло не так!")
        }*/
    }

    private fun generateSimpleOutputString(recipe: IntArray): String? {
        val output: String
        val str = StringBuilder()
        for (i in 0 until recipe.size - 1) {
            str.append(i + 1).append(",").append(recipe[i]).append(":")
        }
        output = str.toString() + recipe.size + "," + recipe[recipe.size - 1] + "."
        return output
    }

    fun getNames(list: List<Ingredient>?): Array<String?> {
        val array = arrayOfNulls<String>(list!!.size + 1)
        array[0] = "Пусто"
        for (i in 1 until list.size + 1)
            array[i] = list[i - 1].name
        return array
    }

    fun onSaveButtonClickToEdit(recipeID: Long, name: String, ingredients: ArrayList<Triple<String, Double, Int>>) {
        val type = getRecipeType(ingredients)
        val newRecipe = Recipe(recipeId = recipeID, name = name, type = type)
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.deleteConnection(recipeID.toString())
                database.updateRecipeName(newRecipe)
                for (i in ingredients.indices) {
                    val connection = Connection(recID = recipeID, ingName = ingredients[i].first, volume = ingredients[i].second, layer = ingredients[i].third)
                    database.insertConnection(connection)
                }
            }
        }
    }

    fun onSaveButtonClickToAdd(name: String, ingredients: ArrayList<Triple<String, Double, Int>>) {
        val type = getRecipeType(ingredients)
        val newRecipe = Recipe(name = name, type = type)
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val id = database.insertRecipeName(newRecipe)
                for (i in ingredients.indices) {
                    val connection = Connection(recID = id, ingName = ingredients[i].first, volume = ingredients[i].second, layer = ingredients[i].third)
                    database.insertConnection(connection)
                }
            }
        }
    }

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

    fun getIngredients(id: Long, connections: List<Connection>): ArrayList<Triple<String, Double, Int>> {
        val array = ArrayList<Triple<String, Double, Int>>()
        for (i in connections.indices) {
            Log.d("DATABASE", "Connections not null")
            if (connections[i].recID == id) array.add(Triple(connections[i].ingName, connections[i].volume, connections[i].layer))
        }
        return array
    }

    private fun filter(simple: Boolean, layer: Boolean): LiveData<List<Recipe>> {
        return if (simple && layer) database.getRecipes()
        else if (simple && !layer) database.filterRecipes("simple")
        else if (!simple && layer) database.filterRecipes("layer")
        else {
            val ld = MutableLiveData<List<Recipe>>()
            ld.value = emptyList()
            ld
        }
    }

    fun filterSimple() {
        _simpleFilter.value = !_simpleFilter.value!!
        recipes = filter(simpleFilter.value!!, layerFilter.value!!)
    }

    fun filterLayer() {
        _layerFilter.value = !_layerFilter.value!!
        recipes = filter(simpleFilter.value!!, layerFilter.value!!)
    }

    fun getButtonStatus(id: Long): Boolean{
        _buttonID.value = id
        return buttonStatus
    }


    fun isActive(ingredients: ArrayList<Triple<String, Double, Int>>) {
            val setting = smartBartender.getMotors()
            for(i in ingredients.indices){
                if(setting.indexOf(ingredients[i].first)==-1) {
                    buttonStatus = false
                    return
                }
            }
            buttonStatus = true
    }
}
