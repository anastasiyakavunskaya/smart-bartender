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
import static com.example.user.SmartBartender.DatabaseHelper.KEY_TYPE;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_VOLUME;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_ING_REC;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_RECIPES;

public class EditModel {

    private final DatabaseHelper dbHelper;

    public EditModel(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    void onSavePressed(boolean isLayer, String name, String oldName, ArrayList<Ingredient> list){
        onDeletePressed(oldName);
        onAddPressed(name, list, isLayer);
    }

    void onAddPressed(String name, ArrayList<Ingredient> list, boolean isLayer) {

        String selectRecId = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = ";
            //recipe name add to table of recipes
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            if(isLayer) contentValues.put(KEY_TYPE, 1);
            else contentValues.put(KEY_TYPE, 0);
            contentValues.put(KEY_NAME, name);
            db.insert(TABLE_RECIPES, null, contentValues);
            contentValues.clear();

            int recID = getID(selectRecId, name);
                for (int i = 0; i <list.size(); i++) {
                    String ingName = list.get(i).name;
                    if(!ingName.equals("Пусто")){
                            int ingValue = list.get(i).value;
                        String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";
                        int ingID = getID(selectIngId, ingName);
                            int ingLayer = list.get(i).layer;

                            contentValues.put(KEY_RECIPES_ID, recID);
                            contentValues.put(KEY_INGREDIENTS_ID, ingID);
                            contentValues.put(KEY_VOLUME, ingValue);
                            contentValues.put(KEY_LAYER, ingLayer);

                            db.insert(TABLE_ING_REC, null, contentValues);
                            contentValues.clear();
                    }
                }
                dbHelper.close();
    }

    void onDeletePressed(String item){

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

    ArrayList <String> getListOfRecipes(){

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_RECIPES, null, null, null, null, null, KEY_NAME);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        } else
            //list.add(warning);
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
        int output = c.getInt(c.getColumnIndex(KEY_ID));
        c.close();
        return output;
    }

    List<Ingredient> getItems(String item) {

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
                    int layer = c1.getInt((c1.getColumnIndex(KEY_LAYER)));

                    String selectName = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = " + "\""+ id + "\"";
                    Cursor c2 = db.rawQuery(selectName, null);
                    if((c2!=null)&&(c2.moveToFirst())) {
                        do {
                            int index = (c2.getColumnIndex(KEY_NAME));
                            String name = c2.getString(index);
                            Ingredient ing = new Ingredient(value, name, id, layer);
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

    public String getItemById(int id){
        String query = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = " + "\"" + id + "\"";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = database.rawQuery(query,null);
        if (c != null)
            c.moveToFirst();
        assert c != null;
        String output = c.getString(c.getColumnIndex(KEY_NAME));
        c.close();
        return output;
    }

    //settings
    public void saveIngredient(String oldName, String name){
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

    public void deleteIngredient(String item){
        dbHelper.getReadableDatabase().delete(TABLE_INGREDIENTS,"name = ?", new String[] {item});
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INGREDIENTS_ID,-1);
        dbHelper.close();
    }

}
