package com.example.user.bartender

import android.app.Application
import android.bluetooth.BluetoothAdapter
import com.example.user.bartender.database.Ingredient

class SmartBartender : Application(){
    val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    val ingredient = Ingredient("Пусто", 1500)
    private var settings = ArrayList<Ingredient>()
    init {
        for(i in 0..5){
            settings.add(ingredient)
        }
    }

    fun setMotors(motors: ArrayList<Ingredient>) {
        this.settings = motors
    }

    fun getMotors(): ArrayList<Ingredient> {
        return settings
    }

    fun clearSettings() {
        settings.clear()
        for(i in 0..5){
            settings.add(ingredient)
        }
    }
}
