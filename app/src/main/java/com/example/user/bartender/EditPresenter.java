/*
package com.example.user.bartender;

import java.util.ArrayList;

class EditPresenter {

    private final EditModel model;
    private EditSimpleRecipesFragment simpleFragment;
    private EditLayerRecipesFragment layerFragment;
    private ItemActivity itemActivity;

    EditPresenter(EditModel model) {
        this.model = model;
    }

    void attachSimpleFragment(EditSimpleRecipesFragment editFragment){
        simpleFragment = editFragment;
    }

    void attachLayerFragment(EditLayerRecipesFragment editFragment){
        layerFragment = editFragment;
    }

    void attachItemActivity(ItemActivity activity){
        itemActivity = activity;
    }

    ArrayList<String> getIngredients(){
        return model.getListOfIngredients();
    }

    void onSaveRecipePressed( String recipeName, String oldName, ArrayList<Ingredient> list, boolean isLayer){
        if(recipeName.length()!=0){
                model.onSavePressed(isLayer, recipeName, oldName, list);
                if(isLayer)layerFragment.onDestroy();
                else simpleFragment.onDestroy();
                itemActivity.recreate();
        }else
            if(isLayer)layerFragment.showToast("Введите название рецепта!");
            else simpleFragment.showToast("Введите название рецепта!");
    }

    void onAddRecipePressed(String recipe, ArrayList<Ingredient> ingredients, boolean isLayer){
        if(isLayer){
            if((recipe.length()!=0)&&(isUnique(recipe))) {
                model.onAddPressed(recipe,ingredients,true);
                itemActivity.recreate();
            }
            else layerFragment.showToast("Невозможно сохранить рецепт с таким именем!");
        }
        else{
            if((recipe.length()!=0)&&(isUnique(recipe))) {
                model.onAddPressed(recipe,ingredients,false);
                itemActivity.recreate();
            }
            else simpleFragment.showToast("Невозможно сохранить рецепт с таким именем!");
        }
    }

    void onDeleteRecipePressed( String item){
        model.onDeletePressed(item);
        itemActivity.recreate();
    }

    boolean isSpinnersCorrect(ArrayList<Ingredient> list){
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.size();j++){
                if((i!=j)&&(!list.get(i).name.equals("Пусто"))&&(list.get(i).name.equals(list.get(j).name)))return false;
            }
        }
        return true;
    }

    private boolean isUnique(String item){
        ArrayList<String> list = model.getListOfRecipes();
        for (int i = 0; i<list.size();i++){
            if(list.get(i).equals(item)) return false;
        }
        return true;
    }

}
*/
