package com.example.user.SmartBartender;



import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.example.user.SmartBartender.SettingsFragments.IngredientsFragment;

import java.util.ArrayList;

class EditPresenter {

    private final EditModel model;
    private EditFragment fragment;

    EditPresenter(EditModel model) {
        this.model = model;
    }

    void attach(EditFragment editFragment){
        fragment = editFragment;
    }


    ArrayList<String> getIngredients(){
        return model.getListOfIngredients();
    }

    void onSaveRecipePressed( String recipeName, String oldName, ArrayList<Ingredient> list, boolean isLayer){
        if(recipeName.length()!=0){
            if(isUnique(recipeName, false))
                if((!list.isEmpty())) model.onSavePressed(false, recipeName, oldName, list, isLayer);
                else fragment.showToast("Введите, пожалуйста, ингредменты!");
            else fragment.showToast("Рецепт с таким именем уже существует!");
        }else fragment.showToast("Введите название рецепта!");


    }

    void onSaveIngredientPressed(String ingredientName, String oldName){
        if(ingredientName.length()!=0){
            if(isUnique(ingredientName, false))model.onSavePressed(true,ingredientName, oldName, null, false);
            else fragment.showToast("Рецепт с таким именем уже существует!");
        }else fragment.showToast("Введите название рецепта!");
    }

    void onAddRecipePressed(String recipe, ArrayList<Ingredient> ingredients, boolean isLayer){
        if(recipe.length()!=0){
            if(isUnique(recipe, false))
                if((!ingredients.isEmpty())) model.onAddPressed(false, recipe,ingredients, isLayer);
                else fragment.showToast("Введите, пожалуйста, ингредменты!");
            else fragment.showToast("Рецепт с таким именем уже существует!");
        }else fragment.showToast("Введите название рецепта!");

    }

    void onAddIngredientPressed(String name){
        if(isUnique(name, true))
            if(name.length()!=0) model.onAddPressed(true, name,null, false);
            else fragment.showToast("Введите название ингредиента!");
        else fragment.showToast("Ингредиент с таким именем уже существует!");
    }

    void onDeleteIngredientPressed( String name){
        model.onDeletePressed(true, name);
    }

    void onDeleteRecipePressed( String oldName){
        model.onDeletePressed(false, oldName);
    }

    boolean isSpinnersCorrect(ArrayList<Ingredient> list){
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.size();j++){
                if((i!=j)&&(!list.get(i).name.equals("Пусто"))&&(list.get(i).name.equals(list.get(j).name)))return false;
            }
        }
        return true;
    }

    boolean isChecked(String item){
        return model.isChecked(item);
    }

    private boolean isUnique(String item, boolean isIngredient){
        ArrayList<String> list;
        if(isIngredient) list = model.getListOfIngredients();
        else list = model.getListOfRecipes();
        for (int i = 0; i<list.size();i++){
            if(list.get(i).equals(item)) return false;
        }
        return true;
    }

}
