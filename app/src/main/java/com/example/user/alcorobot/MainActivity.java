package com.example.user.alcorobot;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


import static com.example.user.alcorobot.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.alcorobot.DatabaseHelper.KEY_NAME;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_INGREDIENTS;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    DatabaseHelper dbHandler;
    int coefficient;


    public RecyclerView initRecycleView(RecyclerView recycler) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(mLayoutManager);
        return recycler;
    }

    public ArrayList<String> readDatabase( String table, String e) {
        dbHandler = new DatabaseHelper(this);
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(table, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        } else
            list.add(e);
        cursor.close();
        return list;
    }

    public void restartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);

    }

    public static EditFragment newInstance(String item, int res, int itemName, boolean isIngredient, ArrayList<String> items) {
        EditFragment f = new EditFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("item", item);
        args.putInt("res", res);
        args.putInt("itemName", itemName);
        args.putBoolean("isIngredient", isIngredient);
        if (!isIngredient) {
            args.putStringArrayList("ingredients", items);
        }
        f.setArguments(args);
        return f;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
        items.clear();
    }

    public static AddFragment newInstance(boolean isIngredient, ArrayList<String> items) {

        AddFragment f = new AddFragment();
        Bundle args = new Bundle();
        if (isIngredient) {
            args.putString("title", "Добавить ингредиент");
            args.putInt("resource", R.layout.ingredients_add_layout);
        } else {
            args.putString("title", "Добавить рецепт");
            args.putInt("resource", R.layout.recipes_add_layout);
            args.putStringArrayList("ingredients", items);
        }
        args.putBoolean("isIngredient", isIngredient);
        f.setArguments(args);
        return f;
    }
    public static SettingsFragment newInstance(ArrayList<String> items, ArrayList<Integer> settings, int coefficient) {

        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("allIngredients", items);
        args.putIntegerArrayList("settingIngredients", settings);
        args.putInt("c", coefficient);
        fragment.setArguments(args);
        return fragment;
    }


    public void onFloatingActionButtonClick(FloatingActionButton addButton, final boolean isIngredient, final ArrayList<String> items) {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                AddFragment myDialogFragment = newInstance(isIngredient, items);

                myDialogFragment.show(manager, "dialog");
                dbHandler.close();
            }
        });
    }

    public boolean isUnique(String item, SQLiteDatabase database, String tableName) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT  * FROM " + tableName + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
        Cursor c = database.rawQuery(query, null);
        if ((c != null) && (c.moveToFirst())) {
            do {
                int index = (c.getColumnIndex(KEY_NAME));
                int id = c.getInt(index);
                ids.add(id);
            } while (c.moveToNext());
        }
        return ids.isEmpty();
    }
    public void callSettingsFragment(){
        ArrayList<String> ingredients = readDatabase(TABLE_INGREDIENTS,"Нет ингредиентов");
        ArrayList<Integer> settings = readSettings();
        int coefficient = settings.get(6);
        settings.remove(6);
        SettingsFragment settingsFragment = newInstance(ingredients, settings, coefficient);
        FragmentManager fm = getSupportFragmentManager();
        settingsFragment.show(fm,"settings");
    }

    private ArrayList<Integer> readSettings() {
        ArrayList<Integer> list = new ArrayList<>();
        dbHandler = new DatabaseHelper(this);
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_SETTINGS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_INGREDIENTS_ID);
            do {
                list.add(cursor.getInt(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> readRecipes( String table, String e) {
        dbHandler = new DatabaseHelper(this);
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHandler.getReadableDatabase();



        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(table, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        } else
            list.add(e);
        cursor.close();
        return list;
    }
}

