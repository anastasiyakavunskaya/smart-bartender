package com.example.user.alcorobot;


import java.util.ArrayList;

class EditPresenter {

    private EditModel model;

    EditPresenter(DatabaseHelper dbHelper) {
        model = new EditModel(dbHelper);
    }

    ArrayList<String> getIngredients(){
        return model.getListOfIngredients();
    }

    void onSaveRecipePressed( String recipeName, String oldName, ArrayList<Ingredient> list){
        model.onSavePressed(false, recipeName, oldName, list);
    }

    void onSaveIngredientPressed(String ingredientName, String oldName){
        model.onSavePressed(true,ingredientName, oldName, null);
    }

    void onAddRecipePressed(String recipe, ArrayList<Ingredient> ingredients){
        model.onAddPressed(false, recipe,ingredients);
    }

    void onAddIngredientPressed(String name){
        model.onAddPressed(true, name,null);
    }

    void onDeleteIngredientPressed( String name){
        model.onDeletePressed(true, name);
    }

    void onDeleteRecipePressed( String oldName){
        model.onDeletePressed(false, oldName);
    }


}
