package com.example.user.bartender.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.user.bartender.SmartBartender
import com.example.user.bartender.database.BartenderDatabase
import com.example.user.bartender.database.Ingredient

class MotorsViewModel(application: Application) :AndroidViewModel(application){
    val database = BartenderDatabase.getInstance(application).bartenderDatabaseDao
    private val smartBartender: SmartBartender = application as SmartBartender

    var ingredients = database.getAllIngredients()

    fun onSetButtonClick(ingredientNameList: List<String>){
        val ingredientList = ArrayList<Ingredient>(6)
        for(i in ingredientNameList.indices){
            val name = ingredientNameList[i]
            ingredientList.add(getIngredientByName(name))
        }
        smartBartender.setMotors(motors = ingredientList)
    }
    private fun getIngredientByName(name: String): Ingredient {
        var ingredient = Ingredient("Пусто", 0)
        val allIngredientsList = ingredients.value!!
        for(i in allIngredientsList.indices){
            if(allIngredientsList[i].name == name) ingredient = allIngredientsList[i]
        }
        return ingredient
    }
    fun onClearButtonClick(){
        smartBartender.clearSettings()
    }
}