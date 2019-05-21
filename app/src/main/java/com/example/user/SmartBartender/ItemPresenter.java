package com.example.user.SmartBartender;


import android.os.Bundle;

class ItemPresenter {

    private ItemActivity view;
    private final ItemModel model;
    private final boolean isIngredient;

    ItemPresenter(ItemModel model, boolean isIngredient) {
        this.model = model;
        this.isIngredient = isIngredient;
    }

    void attachView(ItemActivity itemActivity) {
        view = itemActivity;
    }

    void viewIsReady() {
        if(isIngredient) view.showItems(model.loadItems());
        else view.showItems(model.loadItemsReadyToCook());
    }

    EditFragment newInstance(boolean isIngredient, String title, String item){
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putBoolean("isIngredient", isIngredient);
        args.putString("title", title);
        args.putString("item", item);
        if (isIngredient) {
            args.putInt("resource", R.layout.ingredients_add_layout);
        } else {
            args.putInt("resource", R.layout.recipes_add_layout);
        }
        fragment.setArguments(args);
        return fragment;
    }

    void onCookClick(String item){
        model.onCookClick(item);
    }






}
