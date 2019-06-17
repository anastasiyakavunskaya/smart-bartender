package com.example.user.SmartBartender.SettingsFragments;
import android.os.Bundle;
import com.example.user.SmartBartender.EditFragment;
import com.example.user.SmartBartender.EditModel;
import com.example.user.SmartBartender.R;
import java.util.ArrayList;

import static com.example.user.SmartBartender.DatabaseHelper.KEY_NAME;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;


class SettingsPresenter {
    private final EditModel model;
    private IngredientsFragment fragment;
    private final String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";

    SettingsPresenter(EditModel model) {
        this.model = model;
    }

    ArrayList<String> getAllIngredients(){
        return model.getListOfIngredients();
    }

    public int getIngredientId(String item){
        if(item.equals("Пусто")) return 0;
        else return model.getID(selectIngId,item);
    }

    public String getIngredientById(int id){
        if(id!=0) return model.getItemById(id);
        else return "Пусто";
    }

    String getCoefficient(){
        return String.valueOf(model.getCoefficient());
    }

    void attachFragment(IngredientsFragment fg) {
        fragment = fg;
    }
    void fragmentIsReady() {
        String str = "Список ингредиентов пока пуст";
        if((model.loadItems()).isEmpty()) {
            fragment.showItems(model.loadItems());
            fragment.showText(str);
        }
        else {
            fragment.showText("");
            fragment.showItems(model.loadItems());
        }
    }
    void deleteItemFromList(String item){
        model.deleteItem(item);
    }
    void saveItemToList(String oldName, String item){
        model.saveItem(oldName, item);
    }
}
