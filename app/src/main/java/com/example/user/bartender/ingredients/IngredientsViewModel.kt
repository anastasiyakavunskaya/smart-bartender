package com.example.user.bartender.ingredients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.user.bartender.database.BartenderDatabaseDao
import com.example.user.bartender.database.Ingredient
import kotlinx.coroutines.*

class IngredientsViewModel(
        val database: BartenderDatabaseDao,
        application: Application):AndroidViewModel(application){
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)
    val ingredients = database.getAllIngredients()


    fun onSaveButtonClick(oldIngredient:Ingredient, name: String, c: Int){
        uiScope.launch {
            withContext(Dispatchers.IO){
                val n = database.updateIngredient(Ingredient (name, c))
                if(n!=1) {
                    database.deleteIngredient(oldIngredient)
                    database.insertIngredient(Ingredient(name, c))
                }
            }
        }
    }

    fun delete(ingredient: Ingredient) {
        uiScope.launch {
            withContext(Dispatchers.IO){
                database.deleteIngredient(ingredient)
            }
        }
    }

}