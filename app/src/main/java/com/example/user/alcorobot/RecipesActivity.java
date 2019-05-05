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
import android.widget.Toast;


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

public class RecipesActivity extends MainActivity implements AddFragment.AddDialogListener, EditFragment.InfoDialogListener {

    public static final String REC_E = "Нет ни одного рецепта!";
    ArrayList<String> ingredientsNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        items = readDatabase(TABLE_RECIPES, REC_E);

        ingredientsNames = readRecipes(TABLE_INGREDIENTS, REC_E);
        ingredientsNames.add(0, null);

        RecyclerView recycler = initRecycleView((RecyclerView) findViewById(R.id.rec_recycler_view));

        recycler.setAdapter(new ItemAdapter(items, new ItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(String item) {
                FragmentManager manager = getSupportFragmentManager();
                EditFragment editFragment = newInstance(item, R.layout.recipes_add_layout, R.id.recipe_name, false, ingredientsNames);
                editFragment.show(manager, "dialog");
            }
        }, false));

        onFloatingActionButtonClick((FloatingActionButton) findViewById(R.id.rec_add_button), false, ingredientsNames);

    }

    @Override
    public void onAddButtonClick(View view, List<Ingredient> ingredients) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String recName = ((EditText) view.findViewById(R.id.recipe_name)).getText().toString();

        //Select Queries
        String selectRecId = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = ";
        String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";

        if((!recName.equals(" "))&&(isUnique(recName,db,TABLE_RECIPES))){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME, recName);
            db.insert(TABLE_RECIPES, null, contentValues);
            contentValues.clear();
            int recID = getID(selectRecId, recName, db);
            if (!ingredients.isEmpty()) {
                for (int i = 0; i < ingredients.size(); i++) {
                    String ingName = ingredients.get(i).name;
                    int ingValue = ingredients.get(i).value;
                    int ingID = getID(selectIngId, ingName, db);
                    contentValues.put(KEY_RECIPES_ID, recID);
                    contentValues.put(KEY_INGREDIENTS_ID, ingID);
                    contentValues.put(KEY_VALUE, ingValue);
                    db.insert(TABLE_ING_REC, null, contentValues);
                    contentValues.clear();
                }
                dbHandler.close();
                restartActivity(RecipesActivity.class);
            }
        } else
            Toast.makeText(getApplicationContext(),"Что-то не так с названием",Toast.LENGTH_LONG).show();
    }

    public int getID(String query, String name, SQLiteDatabase db) {
        query += "\"" + name + "\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null)
            c.moveToFirst();
        assert c != null;
        return c.getInt(c.getColumnIndex(KEY_ID));
    }

    @Override
    public void onDeleteButtonClick(View view, String item) {
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
        Cursor c = database.rawQuery(query, null);
        if ((c != null) && (c.moveToFirst())) {
                int index = (c.getColumnIndex(KEY_ID));
                int id = c.getInt(index);
                String idSelect = String.valueOf(id);
                database.delete(TABLE_ING_REC, "rec_id = ?", new String [] {idSelect});
                database.delete(TABLE_RECIPES,"name = ?", new String[] {item});
        }

        dbHandler.close();
        restartActivity(RecipesActivity.class);
    }

    @Override
    public void onSaveButtonClick(View view, String item, List<Ingredient> list) {
        onDeleteButtonClick(view, item);
        onAddButtonClick(view,list);
    }

    @Override
    public List<Ingredient> getItems(String item) {
        List<Ingredient> ingredients = new ArrayList<>();

        dbHandler = new DatabaseHelper(this);
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item +"\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
            int recId = c.getInt(c.getColumnIndex(KEY_ID));
            String selectId = "SELECT  * FROM " + TABLE_ING_REC + " WHERE " + KEY_RECIPES_ID + " = " + "\"" + recId + "\"";

            Cursor c1 = db.rawQuery(selectId, null);
            if ((c1 != null)&&(c1.moveToFirst())) {
                do {
                    int id = c1.getInt((c1.getColumnIndex(KEY_INGREDIENTS_ID)));
                    int value = c1.getInt((c1.getColumnIndex(KEY_VALUE)));
                    String selectName = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = " + "\""+ id + "\"";
                    Cursor c2 = db.rawQuery(selectName, null);
                    if((c2!=null)&&(c2.moveToFirst())) {
                        do {
                            int index = (c2.getColumnIndex(KEY_NAME));
                            String name = c2.getString(index);
                            Ingredient ing = new Ingredient(value, name, id);
                            ingredients.add(ing);
                        } while (c2.moveToNext());
                    }
                } while (c1.moveToNext());
            }
        }
        return ingredients;
    }


}