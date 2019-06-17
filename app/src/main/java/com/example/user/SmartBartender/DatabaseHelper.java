package com.example.user.SmartBartender;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipesDatabase";
    //Table Names
    public static final String TABLE_INGREDIENTS = "ingredients";
    static final String TABLE_RECIPES = "recipes";
    static final String TABLE_ING_REC = "ingredients_recipes";
    //Common column names
    static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    //Ingredients_recipes - column names
    static final String KEY_INGREDIENTS_ID = "ing_id";
    static final String KEY_RECIPES_ID = "rec_id";
    static final String KEY_VOLUME = "volume";

    //Recipes - column name
    static final String KEY_LAYER = "layer";


    //Table Create Statements
    private static final String CREATE_TABLE_INGREDIENTS = "CREATE TABLE " + TABLE_INGREDIENTS + "(" + KEY_ID + " integer primary key," + KEY_NAME + " text" + ")";
    private static final String CREATE_TABLE_RECIPES = "CREATE TABLE " + TABLE_RECIPES + "(" + KEY_ID + " integer primary key," + KEY_NAME + " text," + KEY_LAYER + " integer" +")";
    private static final String CREATE_TABLE_ING_REC = "CREATE TABLE " + TABLE_ING_REC + "(" + KEY_ID + " integer primary key," + KEY_INGREDIENTS_ID + " integer," + KEY_RECIPES_ID + " integer," + KEY_VOLUME + " integer" + ")";


    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INGREDIENTS);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_ING_REC);
        ContentValues values = new ContentValues();
        values.put(KEY_INGREDIENTS_ID,-1);

        values.clear();
        values.put(KEY_INGREDIENTS_ID,1);
        values.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_INGREDIENTS);
        db.execSQL("drop table if exists " + TABLE_RECIPES);
        db.execSQL("drop table if exists " + TABLE_ING_REC);
        onCreate(db);
    }
}
