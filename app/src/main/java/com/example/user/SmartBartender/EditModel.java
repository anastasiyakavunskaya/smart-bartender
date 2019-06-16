package com.example.user.SmartBartender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.SmartBartender.DatabaseHelper.KEY_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_LAYER;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_NAME;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_RECIPES_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_VOLUME;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_ING_REC;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_RECIPES;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_SETTINGS;

public class EditModel {

    private final DatabaseHelper dbHelper;
    private final String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";


    public EditModel(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void onSavePressed(boolean isIngredient, String name, String oldName, ArrayList<Ingredient> list, boolean isLayer){
        if(isIngredient){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME,name);
            dbHelper.getWritableDatabase().update(TABLE_INGREDIENTS, contentValues,"name = ?", new String[] {oldName});
            dbHelper.close();
            //presenter.restartActivity();
        }
        else{
            onDeletePressed(false,oldName);
            onAddPressed(false, name, list, isLayer);
        }
    }
    void onAddPressed(boolean isIngredient, String name, ArrayList<Ingredient> list, boolean isLayer) {
        String selectRecId = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = ";
        if (isIngredient) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_NAME, name);
                dbHelper.getReadableDatabase().insert(TABLE_INGREDIENTS, null, contentValues);
                dbHelper.close();
        } else {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME, name);
            if(isLayer)contentValues.put(KEY_LAYER, 1);
            else contentValues.put(KEY_LAYER,0);
            db.insert(TABLE_RECIPES, null, contentValues);
            contentValues.clear();
            int recID = getID(selectRecId, name);
                for (int i = 0; i <list.size(); i++) {
                    String ingName = list.get(i).name;
                    if(!ingName.equals("Пусто")){
                            int ingValue = list.get(i).value;
                            int ingID = getID(selectIngId, ingName);
                            contentValues.put(KEY_RECIPES_ID, recID);
                            contentValues.put(KEY_INGREDIENTS_ID, ingID);
                            contentValues.put(KEY_VOLUME, ingValue);
                            db.insert(TABLE_ING_REC, null, contentValues);
                            contentValues.clear();
                    }
                }
                dbHelper.close();
        }
    }
    public void onDeletePressed(boolean isIngredient, String item){
        if(isIngredient){
            int id = getID(selectIngId,item);
            dbHelper.getReadableDatabase().delete(TABLE_INGREDIENTS,"name = ?", new String[] {item});
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_INGREDIENTS_ID,-1);
            dbHelper.getReadableDatabase().update(TABLE_SETTINGS,contentValues,"ing_id = ?", new String [] {String.valueOf(id)});
            dbHelper.close();
        }
        else
        {
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
            Cursor c = database.rawQuery(query, null);
            if ((c != null) && (c.moveToFirst())) {
                int index = (c.getColumnIndex(KEY_ID));
                int id = c.getInt(index);
                String idSelect = String.valueOf(id);
                database.delete(TABLE_ING_REC, "rec_id = ?", new String [] {idSelect});
                database.delete(TABLE_RECIPES,"name = ?", new String[] {item});
                c.close();
            }
            dbHelper.close();
            //presenter.restartActivity();
        }

    }

    public ArrayList <String> getListOfIngredients(){
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_INGREDIENTS, null, null, null, null, null, KEY_NAME);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public ArrayList <String> getListOfRecipes(){
        String warning = "Список рецептов пуст";
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_RECIPES, null, null, null, null, null, KEY_NAME);
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

    public ArrayList<String> loadItems() {
        //String warning = "Пусто, добавьте первый ингредиент";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_INGREDIENTS, null, null, null, null, null, KEY_NAME);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int getID(String query, String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        query += "\"" + name + "\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null)
            c.moveToFirst();
        assert c != null;
        return c.getInt(c.getColumnIndex(KEY_ID));
    }

    public List<Ingredient> getItems(String item) {

        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item +"\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
            int recId = c.getInt(c.getColumnIndex(KEY_ID));
            String selectId = "SELECT  * FROM " + TABLE_ING_REC + " WHERE " + KEY_RECIPES_ID + " = " + "\"" + recId + "\"";
            c.close();

            Cursor c1 = db.rawQuery(selectId, null);
            if ((c1 != null)&&(c1.moveToFirst())) {
                do {
                    int id = c1.getInt((c1.getColumnIndex(KEY_INGREDIENTS_ID)));
                    int value = c1.getInt((c1.getColumnIndex(KEY_VOLUME)));
                    String selectName = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = " + "\""+ id + "\"";
                    Cursor c2 = db.rawQuery(selectName, null);
                    if((c2!=null)&&(c2.moveToFirst())) {
                        do {
                            int index = (c2.getColumnIndex(KEY_NAME));
                            String name = c2.getString(index);
                            Ingredient ing = new Ingredient(value, name, id);
                            ingredients.add(ing);
                        } while (c2.moveToNext());
                        c2.close();
                    }
                } while (c1.moveToNext());
                c1.close();
            }
        }
        return ingredients;
    }

    public ArrayList<Integer> getSettingsDbIds(){
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
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

    public ArrayList<Integer> getSettingsIds(){
        ArrayList<String> ingredientsList = getListOfIngredients();
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> settingsList = getSettingsDbIds();
        for(int i=0;i<settingsList.size()-1;i++){
            if(settingsList.get(i)!=-1){
                int id = ingredientsList.indexOf(getItemById(getSettingsDbIds().get(i)))+1;
                list.add(id);
            }
            else list.add(i,0);
        }
        return list;
    }

    public String getItemById(int id){
        String query = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = " + "\"" + id + "\"";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(query,null);
        if (c != null)
            c.moveToFirst();
        assert c != null;
        return c.getString(c.getColumnIndex(KEY_NAME));
    }

    public void setIngredientsSettings(int i, String set){
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        if(set.equals("Пусто"))
            contentValues.put(KEY_INGREDIENTS_ID,-1);
        else{
            int id = getID(selectIngId,set);
            contentValues.put(KEY_INGREDIENTS_ID,id);
        }

        database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(i+1)});
        contentValues.clear();

    }

    public void setCoefficientSetting(String coefficient){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INGREDIENTS_ID,coefficient);
        database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(7)});
        contentValues.clear();
    }

    public int getCoefficient(){
        return getSettingsDbIds().get(6);
    }

    public void deleteSettings(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INGREDIENTS_ID,-1);
        for (int i=1;i<7;i++){
            database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(i)});
        }
        contentValues.clear();
        contentValues.put(KEY_INGREDIENTS_ID,1);
        database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(7)});
        contentValues.clear();
    }

    public boolean isChecked(String item){
        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(query,null);
        if (c != null)
            c.moveToFirst();
        assert c != null;
        return (c.getInt(c.getColumnIndex(KEY_LAYER))==1);
    }
    public void saveItem(String oldName, String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME,name);
        if(oldName==null){
            dbHelper.getReadableDatabase().insert(TABLE_INGREDIENTS, null, contentValues);
        }
        else{
            dbHelper.getWritableDatabase().update(TABLE_INGREDIENTS, contentValues,"name = ?", new String[] {oldName});
        }

        dbHelper.close();
    }

    public void deleteItem(String item){
        int id = getID(selectIngId,item);
        dbHelper.getReadableDatabase().delete(TABLE_INGREDIENTS,"name = ?", new String[] {item});
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INGREDIENTS_ID,-1);
        dbHelper.getReadableDatabase().update(TABLE_SETTINGS,contentValues,"ing_id = ?", new String [] {String.valueOf(id)});
        dbHelper.close();
    }

}
