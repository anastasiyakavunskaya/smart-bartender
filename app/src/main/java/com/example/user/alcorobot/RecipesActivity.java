package com.example.user.alcorobot;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

import static com.example.user.alcorobot.DatabaseHelper.KEY_ID;
import static com.example.user.alcorobot.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.alcorobot.DatabaseHelper.KEY_NAME;
import static com.example.user.alcorobot.DatabaseHelper.KEY_RECIPES_ID;
import static com.example.user.alcorobot.DatabaseHelper.KEY_VALUE;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_ING_REC;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_RECIPES;

public class RecipesActivity extends MainActivity  implements AddDialogFragment.AddDialogListener {

   public static final String REC_E = "Нет ни одного рецепта!";
    ArrayList<String> ingredientsNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        dbHandler = new DatabaseHelper(this);
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        items = readDatabase(database, TABLE_RECIPES, REC_E);
        ingredientsNames = readDatabase(database, TABLE_INGREDIENTS, REC_E);
        ingredientsNames.add(0,null);

        RecyclerView recycler = initRecycleView((RecyclerView) findViewById(R.id.rec_recycler_view));
        recycler.setAdapter(new ItemAdapter(items, new ItemAdapter.OnItemClickListener() {
            @Override public void onItemClick(String item) {
                FragmentManager manager = getSupportFragmentManager();
                InfoFragment infoFragment = newInstance(item);
                infoFragment.show(manager,"dialog");
            }
        }));
        onFloatingActionButtonClick((FloatingActionButton) findViewById(R.id.rec_add_button),false, ingredientsNames);
    }

    @Override
    public void onAddButtonClick(View view, List<Ingredient> ingredients) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String recName = ((EditText) view.findViewById(R.id.recipe_name)).getText().toString();

        //Select Queries
        String selectRecId = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = ";
        String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";

        //TODO проверка на пустую строку и на существование такого рецепта
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,recName);
        db.insert(TABLE_RECIPES, null, contentValues);
        contentValues.clear();
        int recID = getID(selectRecId,recName,db);
        if(!ingredients.isEmpty()){
            for (int i =0; i<ingredients.size();i++){
                String ingName = ingredients.get(i).name;
                int ingValue = ingredients.get(i).value;
                int ingID = getID(selectIngId,ingName,db);
                contentValues.put(KEY_RECIPES_ID, recID);
                contentValues.put(KEY_INGREDIENTS_ID, ingID);
                contentValues.put(KEY_VALUE, ingValue);
                db.insert(TABLE_ING_REC,null,contentValues);
                contentValues.clear();
            }
            dbHandler.close();
        }

    }

    public int getID (String query, String name, SQLiteDatabase db){
        query += "\""+name+"\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null)
            c.moveToFirst();
        int id = c.getInt(c.getColumnIndex(KEY_ID));
        return id;
    }

}
