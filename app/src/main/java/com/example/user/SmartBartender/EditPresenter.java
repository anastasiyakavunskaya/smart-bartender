package com.example.user.SmartBartender;

import android.content.Intent;

import java.util.ArrayList;

class EditPresenter {

    private EditModel model;
    private EditFragment fragment;

    EditPresenter(EditModel model) {
        this.model = model;
    }

    ArrayList<String> getIngredients(){
        return model.getListOfIngredients();
    }

    void onSaveRecipePressed( String recipeName, String oldName, ArrayList<Ingredient> list){
        //TODO isSpinnersCorrect
        model.onSavePressed(false, recipeName, oldName, list);
    }

    void onSaveIngredientPressed(String ingredientName, String oldName){
        model.onSavePressed(true,ingredientName, oldName, null);
    }

    void onAddRecipePressed(String recipe, ArrayList<Ingredient> ingredients){
        //TODO isSpinnersCorrect
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

    boolean isSpinnersCorrect(ArrayList<Ingredient> list){
        return true;
    }

}
