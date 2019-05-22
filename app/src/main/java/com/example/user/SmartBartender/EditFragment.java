package com.example.user.SmartBartender;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditFragment extends AppCompatDialogFragment {

    private EditPresenter presenter;
    private String positiveBtn, negativeBtn, title;
    private View view;
    private EditText editText;
    private EditModel model;
    CheckBox checkBox;

    private boolean isIngredient;
    private boolean addition;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        init(inflater);
        if (title.equals("Добавить")) addConfigs();
        else editConfigs();
        if (isIngredient) buildIngredients(builder);
        else buildRecipes(builder);
        return builder.create();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void buildRecipes(AlertDialog.Builder builder) {
        editText = view.findViewById(R.id.recipe_name);
        checkBox = view.findViewById(R.id.checkbox);
        final ArrayList<Spinner> spinners;
        if (!addition) {
            assert getArguments() != null;
            String item = getArguments().getString("item");
            editText.setText(item);
            assert item != null;
            spinners = spinnersSettings(item, presenter.getIngredients(), view);
            checkBox.setChecked(presenter.isChecked(item));
        } else spinners = spinnersSettings("Пусто", presenter.getIngredients(), view);
        builder.setView(view)
                .setTitle(title)
                .setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean isLayer = checkBox.isChecked();
                        ArrayList<Integer> values = getListOfValues(view);
                        String recipe = ((EditText) view.findViewById(R.id.recipe_name)).getText().toString();
                        if (addition)
                            presenter.onAddRecipePressed(recipe, getData(values, spinners), isLayer);
                        else {
                            assert getArguments() != null;
                            presenter.onSaveRecipePressed(recipe, getArguments().getString("item"), getData(values, spinners), isLayer);
                        }
                        onDestroy();
                        restart(false);
                    }
                })
                .setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (addition) onDestroy();
                        else {
                            assert getArguments() != null;
                            presenter.onDeleteRecipePressed(getArguments().getString("item"));
                        }
                        onDestroy();
                        restart(false);
                    }
                });
    }

    private void buildIngredients(AlertDialog.Builder builder) {
        editText = view.findViewById(R.id.ingredient_name);
        if (!addition) {
            assert getArguments() != null;
            editText.setText(getArguments().getString("item"));
        }

        builder.setView(view)
                .setTitle(title)
                .setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newIngredient = ((EditText) view.findViewById(R.id.ingredient_name)).getText().toString();
                        if (addition) presenter.onAddIngredientPressed(newIngredient);
                        else {
                            assert getArguments() != null;
                            presenter.onSaveIngredientPressed(newIngredient, getArguments().getString("item"));
                        }
                        onDestroyView();
                        restart(true);

                    }
                })
                .setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (addition) onDestroy();
                        else {
                            assert getArguments() != null;
                            presenter.onDeleteIngredientPressed(getArguments().getString("item"));
                        }
                        onDestroyView();
                        restart(true);

                    }
                });
    }

    private void init(LayoutInflater inflater) {
        assert getArguments() != null;
        int resource = getArguments().getInt("resource");
        isIngredient = getArguments().getBoolean("isIngredient");
        title = getArguments().getString("title");
        view = inflater.inflate(resource, null);
        model = new EditModel(new DatabaseHelper(getContext()));
        presenter = new EditPresenter(model);
    }

    private void addConfigs() {
        positiveBtn = "Добавить";
        negativeBtn = "Отмена";
        addition = true;
    }

    private void editConfigs() {
        positiveBtn = "Сохранить";
        negativeBtn = "Удалить";
        addition = false;
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Spinner setSpinner(Spinner s, List<String> list) {
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, list);
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

    private ArrayList<Ingredient> getData(ArrayList<Integer> values, List<Spinner> spinners) {
        int ingID = 0;
        ArrayList<Ingredient> ingredientsList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Object ing = spinners.get(i).getSelectedItem();
            if (ing != null) {
                Ingredient ingredient = new Ingredient(values.get(i), ing.toString(), ingID);
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void restart(boolean isIngredient) {
        Intent intent = new Intent(getContext(), ItemActivity.class);
        intent.putExtra("isIngredient", isIngredient);
        Objects.requireNonNull(getActivity()).startActivity(intent);
        getActivity().finish();
    }

}






