package com.example.user.SmartBartender;

import android.os.Bundle;

public class ItemPresenter {

    private ItemActivity view;
    private final ItemModel model;
    private boolean layerRecipes;


    public ItemPresenter(ItemModel model, boolean layerRecipes) {
        this.model = model;
        this.layerRecipes = layerRecipes;
        model.attach(this);
    }

    public void attachView(ItemActivity itemActivity) {
        view = itemActivity;
    }

    public SmartBartender getSettings(){
        return (SmartBartender) view.getApplication();
    }

    public void viewIsReady() {
        if(layerRecipes) view.showItems(model.loadLayerRecipes());
        else view.showItems(model.loadSimpleRecipes());
    }

    public boolean listOfRecipesIsEmpty (boolean layerRecipes){
        return model.listIsEmpty(layerRecipes);
    }

    public EditSimpleRecipesFragment newSimpleInstance(boolean edit, String item) {
        EditSimpleRecipesFragment fragment = new EditSimpleRecipesFragment();
        Bundle args = new Bundle();
        args.putBoolean("edit", edit);
        args.putString("item", item);
        args.putInt("resource", R.layout.simple_recipes_layout);
        fragment.setArguments(args);
        return fragment;
    }

    public EditLayerRecipesFragment newLayerInstance(boolean edit, String item) {
        EditLayerRecipesFragment fragment = new EditLayerRecipesFragment();
        Bundle args = new Bundle();
        args.putBoolean("edit", edit);
        args.putString("item", item);
        args.putInt("resource", R.layout.layer_recipes_layout);
        fragment.setArguments(args);
        return fragment;
    }

    void onCookClick(String item, boolean isLayer) {
        model.onCookClick(item, isLayer);
    }

    public void showToast(String text){
        view.showToast(text);
    }
}
