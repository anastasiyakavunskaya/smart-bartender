package com.example.user.alcorobot;


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
        view.showItems(model.loadItems(isIngredient));
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
}
