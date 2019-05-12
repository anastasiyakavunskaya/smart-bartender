package com.example.user.alcorobot;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import static com.example.user.alcorobot.DatabaseHelper.KEY_NAME;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_RECIPES;

class ItemModel {
    private final DatabaseHelper dbHelper;

    ItemModel(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    ArrayList<String> loadItems(boolean ing) {
        String table, warning;
        if (ing) {
            table = TABLE_INGREDIENTS;
            warning = "Пусто, добавьте первый ингредиент";
        } else {
            table = TABLE_RECIPES;
            warning = "Пусто, добавьте первый рецепт";
        }
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(table, null, null, null, null, null, null);
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

}
