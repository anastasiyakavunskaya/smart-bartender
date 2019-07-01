package com.example.user.SmartBartender.SettingsFragments;
import com.example.user.SmartBartender.EditModel;

import java.util.ArrayList;

import static com.example.user.SmartBartender.DatabaseHelper.KEY_NAME;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;


class SettingsPresenter {
    private final EditModel model;
    private IngredientsFragment fragment;

    SettingsPresenter(EditModel model) {
        this.model = model;
    }

    ArrayList<String> getAllIngredients(){
        return model.getListOfIngredients();
    }

    int getIngredientId(String item){
        String selectIngId = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_NAME + " = ";
        if(item.equals("Пусто")) return 0;
        else return model.getID(selectIngId,item);
    }
    String getIngredientById(int id){
        if(id!=0) return model.getItemById(id);
        else return "Пусто";
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
        model.deleteIngredient(item);
    }
    void saveItemToList(String oldName, String item){
        if(!item.isEmpty()){
            if(isUnique(item)) model.saveIngredient(oldName, item);
            else fragment.showToast("Ингредиент с таким именем уже существует!");
        }
        else fragment.showToast("Введите название!");

    }

    private boolean isUnique(String item){
        ArrayList<String> list = model.getListOfIngredients();
        for (int i = 0; i<list.size();i++){
            if(list.get(i).equals(item)) return false;
        }
        return true;
    }

}
