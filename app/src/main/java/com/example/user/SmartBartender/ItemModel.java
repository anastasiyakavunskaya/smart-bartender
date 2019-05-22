package com.example.user.SmartBartender;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.SmartBartender.DatabaseHelper.KEY_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_NAME;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_RECIPES_ID;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_ING_REC;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_RECIPES;

class ItemModel {
    private final DatabaseHelper dbHelper;

    ItemModel(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    ArrayList<String> loadItems() {
        String warning = "Пусто, добавьте первый ингредиент";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_INGREDIENTS, null, null, null, null, null, KEY_NAME);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        } else
            list.add(warning);
        cursor.close();
        return list;
    }

    private boolean isReadyToCook(String recipeName) {
        EditModel model = new EditModel(dbHelper);
        ArrayList<Integer> settings = model.getSettingsDbIds();
        ArrayList<Integer> recipe = getRecipesIds(recipeName);
        boolean ingredientInList = false;
        for(int i=0;i<recipe.size();i++){
            for(int j=0;j<settings.size();j++){
                if (recipe.get(i).equals(settings.get(j))){
                    ingredientInList = true;
                }
            }
            if(!ingredientInList)return false;
            ingredientInList = false;
        }
        return  true;
    }

    private ArrayList<Integer> getRecipesIds(String item) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
            int recId = c.getInt(c.getColumnIndex(KEY_ID));
            String selectId = "SELECT  * FROM " + TABLE_ING_REC + " WHERE " + KEY_RECIPES_ID + " = " + "\"" + recId + "\"";

            Cursor c1 = database.rawQuery(selectId, null);
            if ((c1 != null) && (c1.moveToFirst())) {
                do {
                    int id = c1.getInt((c1.getColumnIndex(KEY_INGREDIENTS_ID)));
                    list.add(id);
                } while (c1.moveToNext());
                c1.close();
            }
            c.close();
        }
        return list;
    }

    ArrayList<String> loadItemsReadyToCook(){
        String warning = "Пусто, добавьте первый ингредиент";
        ArrayList<String> readyToCookRecipes = new ArrayList<>();
        for(int i=0;i<allRecipes().size();i++){
            if(isReadyToCook(allRecipes().get(i))) readyToCookRecipes.add(allRecipes().get(i));
        }
        if(readyToCookRecipes.isEmpty())readyToCookRecipes.add(warning);

        return readyToCookRecipes;
    }

    private ArrayList<String> allRecipes(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_RECIPES, null, null, null, null, null, KEY_NAME);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    void onCookClick(String item){
        int[] recipe = getRecipe(item);
        String output = generateOutputString(recipe);

    }

    private int[] getRecipe(String item){
         EditModel model = new EditModel(dbHelper);
         List<Ingredient> ingredients = model.getItems(item);
         ArrayList<Integer> settings = model.getSettingsDbIds();
         //Список для последовательноой записи ингредиентов с их объемом
         int[] recipe = new int[6];
         for(int i =0; i<ingredients.size();i++){
             for (int j=0; j<settings.size();j++){
                 if(ingredients.get(i).id == settings.get(j))recipe[j]=ingredients.get(i).value*model.getCoefficient();
             }
         }
         return recipe;
     }

     private String generateOutputString(int[] recipe){
        String str=" ";
        for(int i=0;i<recipe.length;i++){
            //str=+recipe[i];
        }
        return str;
     }

     /*boolean isSettingsEmpty(){
         EditModel model = new EditModel(dbHelper);
         ArrayList<Integer> settings = model.getSettingsDbIds();
         for(int i=0;i<settings.size();i++){
             if (settings.get(i)!=-1) return false;
         }
         return true;
     }*/

}
