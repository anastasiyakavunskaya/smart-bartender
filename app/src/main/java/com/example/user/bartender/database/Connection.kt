package com.example.user.bartender.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connection_table")
data class Connection(
        @PrimaryKey(autoGenerate = true)
        var connectionId:Long = 0L,
        @ColumnInfo(name = "recipe_id")
        var recID: Long = 0L,
        @ColumnInfo(name = "ingredient_id")
        var ingID: Long = 0L,
        @ColumnInfo(name = "volume")
        var volume: Double = 0.0,
        @ColumnInfo(name = "layer")
        var layer: Int = 0
)