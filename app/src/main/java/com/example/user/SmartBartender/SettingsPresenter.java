package com.example.user.SmartBartender;


import java.util.ArrayList;


class SettingsPresenter {
    private final EditModel model;

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
}
