package com.example.user.alcorobot;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

class EditModel {

    private DatabaseHelper dbHelper;
    private EditPresenter presenter;

    EditModel(DatabaseHelper dbHelper) {
        //presenter = new EditPresenter(dbHelper);
        this.dbHelper = dbHelper;
    }

    void onSavePressed(boolean isIngredient, String name, String oldName, ArrayList<Ingredient> list){
        if(isIngredient){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME,name);
            dbHelper.getWritableDatabase().update(TABLE_INGREDIENTS, contentValues,"name = ?", new String[] {oldName});
            dbHelper.close();
            //presenter.restartActivity();
        }
        else{
            onDeletePressed(false,oldName);
            onAddPressed(false, name, list);
        }
    }
    void onAddPressed(boolean isIngredient, String name, ArrayList<Ingredient> list) {
        String selectRecId = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = ";
        String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";

        if (isIngredient) {
            if ((!(name.equals(" "))) && (isUnique(name, dbHelper.getReadableDatabase(), TABLE_INGREDIENTS))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_NAME, name);
                dbHelper.getReadableDatabase().insert(TABLE_INGREDIENTS, null, contentValues);
                dbHelper.close();
            }
            //presenter.showNameError();
            //presenter.restartActivity();
        } else {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if ((!name.equals(" ")) && (isUnique(name, db, TABLE_RECIPES))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_NAME, name);
                db.insert(TABLE_RECIPES, null, contentValues);
                contentValues.clear();
                int recID = getID(selectRecId, name, db);
                if (!getListOfIngredients().isEmpty()) {
                    for (int i = 0; i < getListOfIngredients().size(); i++) {
                        String ingName = list.get(i).name;
                        int ingValue = list.get(i).value;
                        int ingID = getID(selectIngId, ingName, db);
                        contentValues.put(KEY_RECIPES_ID, recID);
                        contentValues.put(KEY_INGREDIENTS_ID, ingID);
                        contentValues.put(KEY_VALUE, ingValue);
                        db.insert(TABLE_ING_REC, null, contentValues);
                        contentValues.clear();
                    }
                    dbHelper.close();
                    //presenter.restartActivity();
                }
                //presenter.showNameError();
            }
        }
    }
    void onDeletePressed(boolean isIngredient, String item){
        if(isIngredient){
            dbHelper.getReadableDatabase().delete(TABLE_INGREDIENTS,"name = ?", new String[] {item});
            dbHelper.close();
            //presenter.restartActivity();
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

    ArrayList <String> getListOfIngredients(){
        String warning = "Список ингредиентов пуст";
        //получаем актуальную версию базы данных
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_INGREDIENTS, null, null, null, null, null, null);
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

    private boolean isUnique(String item, SQLiteDatabase database, String tableName) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT  * FROM " + tableName + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
        Cursor c = database.rawQuery(query, null);
        if ((c != null) && (c.moveToFirst())) {
            do {
                int index = (c.getColumnIndex(KEY_NAME));
                int id = c.getInt(index);
                ids.add(id);
            } while (c.moveToNext());
            c.close();
        }
        return ids.isEmpty();
    }

    private int getID(String query, String name, SQLiteDatabase db) {
        query += "\"" + name + "\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null)
            c.moveToFirst();
        assert c != null;
        c.close();
        return c.getInt(c.getColumnIndex(KEY_ID));
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
                        c2.close();
                    }
                } while (c1.moveToNext());
                c1.close();
            }
        }
        return ingredients;
    }

}
