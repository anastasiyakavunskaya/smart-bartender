package com.example.user.bartender.ingredients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.user.bartender.database.BartenderDatabaseDao
import com.example.user.bartender.database.Ingredient
import kotlinx.coroutines.*

class IngredientsViewModel(
        val database: BartenderDatabaseDao,
        application: Application):AndroidViewModel(application){
    private var viewModelJob = Job()

    private val _editItem = MutableLiveData<Ingredient>()
    val editItem: LiveData<Ingredient> = _editItem

    init {
        _editItem.value = null
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)

    val ingredients = database.getAllIngredients()

    fun onAddClick( name:String, c:Double = 1500.0){
        uiScope.launch {
            val newIngredient = Ingredient(c = c,name = name)
            insert(newIngredient)
        }
    }
    private suspend fun insert(ingredient:Ingredient){
        withContext(Dispatchers.IO){
            database.insertIngredient(ingredient)
        }
    }

    fun onItemClick(ingredient: Ingredient) {
        _editItem.value = ingredient
    }

    fun onEditClick(oldIngredient: Ingredient, name: String, c: Double) {
        uiScope.launch {
            withContext(Dispatchers.IO){
                val n = database.updateIngredient(Ingredient (name, c))
                if(n!=1) {
                    database.deleteIngredient(oldIngredient)
                    database.insertIngredient(Ingredient(name, c))
                }
            }
        }
        _editItem.value = null
    }

    fun delete(ingredient: Ingredient) {
        uiScope.launch {
            withContext(Dispatchers.IO){
                database.deleteIngredient(ingredient)
            }
        }
    }


}