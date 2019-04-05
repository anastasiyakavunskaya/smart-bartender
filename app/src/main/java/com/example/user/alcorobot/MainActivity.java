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


import static com.example.user.alcorobot.DatabaseHelper.KEY_NAME;

public class MainActivity extends AppCompatActivity  {

    ArrayList<String> items = new ArrayList<>();
    DatabaseHelper dbHandler;

    public RecyclerView initRecycleView(RecyclerView recycler) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(mLayoutManager);
        return recycler;
    }

    public ArrayList<String> readDatabase(SQLiteDatabase database, String table, String e){
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

    public void restartActivity(){
        Intent ingIntent = new Intent(this, IngredientsActivity.class);
        startActivity(ingIntent);
    }

    public static InfoFragment newInstance(String item) {
        InfoFragment f = new InfoFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("item", item);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
        items.clear();
    }

    public static AddDialogFragment newInstance(boolean isIngredient, ArrayList<String> items){

        AddDialogFragment f = new AddDialogFragment();
        Bundle args = new Bundle();
        if(isIngredient){
            args.putString ("title","Добавить ингредиент");
            args.putInt("resource",R.layout.ingredients_add_dialog_layout);
        }
        else
        {
            args.putString ("title","Добавить рецепт");
            args.putInt("resource",R.layout.recipes_add_dialog_layout);
            args.putStringArrayList("ingredients", items);
        }
        args.putBoolean("isIngredient", isIngredient);
        f.setArguments(args);
        return f;
    }


    public void onFloatingActionButtonClick(FloatingActionButton addButton, final boolean isIngredient, final ArrayList<String> items) {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                AddDialogFragment myDialogFragment = newInstance(isIngredient,items);

                myDialogFragment.show(manager, "dialog");
                dbHandler.close();
            }
        });
    }

}

