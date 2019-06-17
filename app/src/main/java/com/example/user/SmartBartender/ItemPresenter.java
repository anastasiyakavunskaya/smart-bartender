package com.example.user.SmartBartender;

import android.os.Bundle;
import android.widget.Toast;

import com.example.user.SmartBartender.SettingsFragments.IngredientsFragment;

public class ItemPresenter {

    private ItemActivity view;
    private IngredientsFragment fragment;
    private final ItemModel model;
    private final boolean isIngredient;

    public ItemPresenter(ItemModel model, boolean isIngredient) {
        this.model = model;
        this.isIngredient = isIngredient;
        model.attach(this);
    }

    public void attachView(ItemActivity itemActivity) {
        view = itemActivity;
    }


    public void viewIsReady() {
        //view.showItems(model.loadItemsReadyToCook());
    }


    public EditFragment newInstance(boolean isIngredient, String title, String item) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putBoolean("isIngredient", isIngredient);
        args.putString("title", title);
        args.putString("item", item);
        args.putInt("resource", R.layout.recipes_add_layout);
        fragment.setArguments(args);
        return fragment;
    }

    void onCookClick(String item) {
        //model.onCookClick(item);
    }
}
