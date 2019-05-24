package com.example.user.SmartBartender;


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
         model.onSavePressed(false, recipeName, oldName, list, isLayer);

    }

    void onSaveIngredientPressed(String ingredientName, String oldName){
        model.onSavePressed(true,ingredientName, oldName, null, false);
    }

    void onAddRecipePressed(String recipe, ArrayList<Ingredient> ingredients, boolean isLayer){
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
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.size();j++){
                if((i!=j)&&(list.get(i).name.equals(list.get(j).name)))return false;
            }
        }
        return true;
    }

    boolean isChecked(String item){
        return model.isChecked(item);
    }

}
