package com.example.user.alcorobot;

import android.content.ContentValues;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import static com.example.user.alcorobot.DatabaseHelper.KEY_NAME;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_INGREDIENTS;

public class IngredientsActivity extends MainActivity implements AddFragment.AddDialogListener, EditFragment.InfoDialogListener{

    public static final String ING_E = "Нет ни одного ингредиента!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        items = readDatabase(TABLE_INGREDIENTS, ING_E);
        RecyclerView recycler = initRecycleView((RecyclerView) findViewById(R.id.ing_recycler_view));


        recycler.setAdapter(new ItemAdapter(items, new ItemAdapter.OnItemClickListener() {
            @Override public void onItemClick(String item) {
                FragmentManager manager = getSupportFragmentManager();
                EditFragment editFragment = newInstance(item, R.layout.ingredients_add_layout, R.id.ingredient_name, true,null);
                editFragment.show(manager,"dialog");
            }
        }, true));
        onFloatingActionButtonClick((FloatingActionButton) findViewById(R.id.ing_add_button),true, null);
    }

    @Override
    public void onAddButtonClick(View view, List<Ingredient> i) {
        EditText editText= view.findViewById(R.id.ingredient_name);
        String name = editText.getText().toString();
        if ((!(name.equals(" ")))&&(isUnique(name,dbHandler.getReadableDatabase(),TABLE_INGREDIENTS)))
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME,name);
            dbHandler.getReadableDatabase().insert(TABLE_INGREDIENTS, null, contentValues);
            dbHandler.close();
        }
        else
            Toast.makeText(getApplicationContext(),"Что-то не так с названием ингредиента!",Toast.LENGTH_LONG).show();
        restartActivity(IngredientsActivity.class);
    }

    @Override
    public void onDeleteButtonClick(View view, String item) {
        dbHandler.getReadableDatabase().delete(TABLE_INGREDIENTS,"name = ?", new String[] {item});
        dbHandler.close();
        restartActivity(IngredientsActivity.class);

    }

    @Override
    public void onSaveButtonClick(View view, String item, List<Ingredient> list) {
        String edName = ((EditText)view.findViewById(R.id.ingredient_name)).getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,edName);
        dbHandler.getWritableDatabase().update(TABLE_INGREDIENTS, contentValues,"name = ?", new String[] {item});
        dbHandler.close();
        restartActivity(IngredientsActivity.class);
    }


    @Override
    public List<Ingredient> getItems(String item) {
        return null;
    }


}
