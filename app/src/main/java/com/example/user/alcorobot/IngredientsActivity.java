package com.example.user.alcorobot;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import static com.example.user.alcorobot.DatabaseHelper.KEY_NAME;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_INGREDIENTS;

public class IngredientsActivity extends MainActivity  implements AddDialogFragment.AddDialogListener, InfoFragment.InfoDialogListener{

    public static final String ING_E = "Нет ни одного ингредиента!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);


        dbHandler = new DatabaseHelper(this);
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        items = readDatabase(database, TABLE_INGREDIENTS, ING_E);

        RecyclerView recycler = initRecycleView((RecyclerView) findViewById(R.id.ing_recycler_view));

        recycler.setAdapter(new ItemAdapter(items, new ItemAdapter.OnItemClickListener() {
            @Override public void onItemClick(String item) {
                FragmentManager manager = getSupportFragmentManager();
                InfoFragment infoFragment = newInstance(item);
                infoFragment.show(manager,"dialog");
            }
        }));
        onFloatingActionButtonClick((FloatingActionButton) findViewById(R.id.ing_add_button),true, null);
    }

    @Override
    public void onAddButtonClick(View view, List<Ingredient> i) {
        EditText name = view.findViewById(R.id.ingredient_name);
        //TODO проверка на ввод пустой строки и на схожесть со списком ингредиентов
        if (!(name.getText().toString().equals(""))){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME,name.getText().toString());
            dbHandler.getReadableDatabase().insert(TABLE_INGREDIENTS, null, contentValues);
            dbHandler.close();
        }
        restartActivity();
    }



    @Override
    public void onDeleteButtonClick(View view, String item) {
        dbHandler.getReadableDatabase().delete(TABLE_INGREDIENTS,"name = ?", new String[] {item});
        dbHandler.close();
        restartActivity();

    }

    @Override
    public void onEditButtonClick(View view, String item) {
        String edName = ((EditText)view.findViewById(R.id.ingredient_name)).getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,edName);
        dbHandler.getWritableDatabase().update(TABLE_INGREDIENTS, contentValues,"name = ?", new String[] {item});
        dbHandler.close();
        restartActivity();

    }

}
