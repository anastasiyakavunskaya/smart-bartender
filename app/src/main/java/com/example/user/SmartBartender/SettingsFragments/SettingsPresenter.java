package com.example.user.SmartBartender.SettingsFragments;
import android.os.Bundle;
import com.example.user.SmartBartender.EditFragment;
import com.example.user.SmartBartender.EditModel;
import com.example.user.SmartBartender.R;
import java.util.ArrayList;


class SettingsPresenter {
    private final EditModel model;
    private IngredientsFragment fragment;

    SettingsPresenter(EditModel model) {
        this.model = model;
    }

    void setSettings(String set, boolean isC, int i){
        if(isC)model.setCoefficientSetting(set);
        else model.setIngredientsSettings(i, set);
    }

    void deleteSettings(){
       model.deleteSettings();
    }

    ArrayList<String> getAllIngredients(){
        return model.getListOfIngredients();
    }

    ArrayList<Integer> getSettingIngredientsId(){
        return model.getSettingsIds();
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
