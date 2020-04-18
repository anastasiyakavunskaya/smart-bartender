package com.example.user.bartender.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Connection::class, Ingredient::class, Recipe::class], version = 1, exportSchema = false)
abstract class BartenderDatabase: RoomDatabase(){

    abstract val bartenderDatabaseDao: BartenderDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: BartenderDatabase? = null

        fun getInstance(context: Context):BartenderDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            BartenderDatabase::class.java,
                            "bartender_database")
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                    return instance
            }
        }
    }
}
