package com.example.user.SmartBartender;


import java.util.ArrayList;

class EditPresenter {

    private final EditModel model;

    EditPresenter(EditModel model) {
        this.model = model;
    }

    ArrayList<String> getIngredients(){
        return model.getListOfIngredients();
    }

    void onSaveRecipePressed( String recipeName, String oldName, ArrayList<Ingredient> list, boolean isLayer){
        //TODO isSpinnersCorrect
        model.onSavePressed(false, recipeName, oldName, list, isLayer);
    }

    void onSaveIngredientPressed(String ingredientName, String oldName){
        model.onSavePressed(true,ingredientName, oldName, null, false);
    }

    void onAddRecipePressed(String recipe, ArrayList<Ingredient> ingredients, boolean isLayer){
        //TODO isSpinnersCorrect
        model.onAddPressed(false, recipe,ingredients, isLayer);
    }

    void onAddIngredientPressed(String name){
        model.onAddPressed(true, name,null, false);
    }

    void onDeleteIngredientPressed( String name){
        model.onDeletePressed(true, name);
    }

    void onDeleteRecipePressed( String oldName){
        model.onDeletePressed(false, oldName);
    }

    boolean isSpinnersCorrect(ArrayList<Ingredient> list){
        return true;
    }

    boolean isChecked(String item){
        return model.isChecked(item);
    }

}
