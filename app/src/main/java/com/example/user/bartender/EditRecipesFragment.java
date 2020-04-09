/*
package com.example.user.bartender;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EditSimpleRecipesFragment extends AppCompatDialogFragment {


    private EditPresenter presenter;
    private String positiveBtn = "Добавить", negativeBtn = "Отмена", title = "Добавить";
    private View view;
    private EditModel model;

    private boolean edit;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        setData(inflater);

        EditText editText = view.findViewById(R.id.recipe_name);
        final ArrayList<Spinner> spinners;
        if (edit) {
            title = "Изменить";
            positiveBtn = "Сохранить";
            negativeBtn = "Удалить";
            assert getArguments() != null;
            String item = getArguments().getString("item");
            editText.setText(item);
            spinners = spinnersSettings(item, presenter.getIngredients(), view);
        }
        else spinners = spinnersSettings("Пусто", presenter.getIngredients(), view);

        builder.setView(view)
                .setTitle(title)
                .setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onPositiveButtonClickSimpleRecipes(spinners);
                    }
                })
                .setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(edit){
                            assert getArguments() != null;
                            presenter.onDeleteRecipePressed(getArguments().getString("item"));
                            onDestroy();
                        }
                        else onDestroy();
                    }
                });
        return builder.create();
    }
    private void setData(LayoutInflater inflater) {
        assert getArguments() != null;
        boolean layerRecipes = getArguments().getBoolean("layerRecipes");
        edit = getArguments().getBoolean("edit");
        if(layerRecipes) view = inflater.inflate(R.layout.layer_recipes_layout, null);
        else view = inflater.inflate(R.layout.fragment_edit_recipes, null);
        model = new EditModel(getContext());
        presenter = new EditPresenter(model);
        presenter.attachSimpleFragment(this);
        presenter.attachItemActivity((ItemActivity) getActivity());
    }

    private ArrayList<Spinner> spinnersSettings(String item, ArrayList<String> ingredients, View view) {
        ArrayList<Spinner> spinners = new ArrayList<>();
        ingredients.add(0, "Пусто");
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.ing1), ingredients));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.ing2), ingredients));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.ing3), ingredients));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.ing4), ingredients));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.ing5), ingredients));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.ing6), ingredients));
        if (!item.equals("Пусто")) setSelections(item, spinners, ingredients, view);
        return spinners;
    }

    private Spinner setSpinner(Spinner s, List<String> list) {
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>((getActivity()), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        s.setOnItemSelectedListener(itemSelectedListener);
        return s;
    }
    private ArrayList<Integer> getListOfValues(View view) {
        ArrayList<Integer> list = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        strings.add(((EditText) view.findViewById(R.id.value1)).getText().toString());
        strings.add(((EditText) view.findViewById(R.id.value2)).getText().toString());
        strings.add(((EditText) view.findViewById(R.id.value3)).getText().toString());
        strings.add(((EditText) view.findViewById(R.id.value4)).getText().toString());
        strings.add(((EditText) view.findViewById(R.id.value5)).getText().toString());
        strings.add(((EditText) view.findViewById(R.id.value6)).getText().toString());
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals("")) list.add(0);
            else list.add(Integer.parseInt(strings.get(i)));
        }
        return list;
    }

    private ArrayList<Ingredient> getSimpleData(ArrayList<Integer> values, List<Spinner> spinners) {
        int ingID = 0;
        ArrayList<Ingredient> ingredientsList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Object ing = spinners.get(i).getSelectedItem();
            if (ing != null) {
                Ingredient ingredient = new Ingredient(values.get(i), ing.toString(), ingID,-1);
                ingredientsList.add(ingredient);
                ingID++;
            }
        }
        return ingredientsList;
    }

    private void setSelections(String item, List<Spinner> spinners, List<String> allIngredients, View view) {
        List<Ingredient> usingIngredients = model.getItems(item);
        List<Integer> resValues = new ArrayList<>();
        resValues.add(R.id.value1);
        resValues.add(R.id.value2);
        resValues.add(R.id.value3);
        resValues.add(R.id.value4);
        resValues.add(R.id.value5);
        resValues.add(R.id.value6);

        for (int i = 0; i < usingIngredients.size(); i++) {
            spinners.get(i).setSelection(allIngredients.indexOf(usingIngredients.get(i).name));
            EditText valueEdit = view.findViewById(resValues.get(i));
            valueEdit.setText(String.valueOf(usingIngredients.get(i).value));
        }
    }

    void showToast(String toast){
        Toast.makeText(getContext(),toast,Toast.LENGTH_SHORT).show();
    }

    void onPositiveButtonClickSimpleRecipes(ArrayList<Spinner> spinners){
        while(true){
            ArrayList<Integer> values = getListOfValues(view);
            String recipe = ((EditText) view.findViewById(R.id.recipe_name)).getText().toString();
            ArrayList<Ingredient> ingredients = getSimpleData(values, spinners);
            if(presenter.isSpinnersCorrect(ingredients)){
                if (edit) {
                    assert getArguments() != null;
                    presenter.onSaveRecipePressed(recipe, getArguments().getString("item"), ingredients, false);
                    break;
                }
                else {
                    presenter.onAddRecipePressed(recipe, ingredients, false);
                    break;
                }
            }
            else Toast.makeText(getContext(),"Введены одинаковые ингредиенты! Попробуйте снова!",Toast.LENGTH_LONG).show();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

}






*/
