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

    public void attachFragment(IngredientsFragment fg) {
        fragment = fg;
    }
    public void fragmentIsReady() {
        String str = "Список ингредиентов пока пуст";
        if((model.loadItems()).isEmpty()) {
            fragment.showItems(model.loadItems());
            fragment.showText(str);
        }
        else {
            fragment.showText("");
            fragment.showItems(model.loadItems());
        }
    }

    public EditFragment newInstance(boolean isIngredient, String title, String item){
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putBoolean("isIngredient", isIngredient);
        args.putString("title", title);
        args.putString("item", item);
            args.putInt("resource", R.layout.recipes_add_layout);
        fragment.setArguments(args);
        return fragment;
    }

    void onCookClick(String item){
        //model.onCookClick(item);
    }
    void showToast(String str){
        Toast.makeText(view,str,Toast.LENGTH_LONG).show();
    }
    public void deleteItemFromList(String item){
        model.deleteItem(item);
    }
    public void saveItemToList(String oldName, String item){
        model.saveItem(oldName, item);
    }
}
