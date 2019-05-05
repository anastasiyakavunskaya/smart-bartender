package com.example.user.alcorobot;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class EditFragment extends AppCompatDialogFragment{


    public interface InfoDialogListener {
        void onDeleteButtonClick(View view, String item);
        void onSaveButtonClick(View view, String item, List<Ingredient> list);
        List<Ingredient> getItems(String item);
    }
    InfoDialogListener  mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        assert getArguments() != null;
        final int res = getArguments().getInt("res");
        final String item = getArguments().getString("item");
        final int itemName = getArguments().getInt("itemName");
        final boolean isIngredient = getArguments().getBoolean("isIngredient");

        final View v = inflater.inflate(res,null);
        final List<Spinner> spinners = new ArrayList<>();

        if(!isIngredient){
            ArrayList<String> ingredients = getArguments().getStringArrayList("ingredients");
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing1),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing2),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing3),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing4),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing5),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing6),ingredients));
            setSelections(item,spinners,ingredients, v);
        }

        EditText editText = v.findViewById(itemName);
        editText.setText(item);

        builder.setView(v)
                .setTitle("Изменение")
                .setPositiveButton ("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!isIngredient) {
                            int ingID = 0;
                            List<Integer> values = getListOfValues(v);
                            List<Ingredient> ingredientsList = new ArrayList<>();
                            for(int i = 0;i<6;i++){
                                Object ing = spinners.get(i).getSelectedItem();
                                if(ing!= null){
                                    Ingredient ingredient = new Ingredient(values.get(i),ing.toString(), ingID);
                                    ingredientsList.add(ingredient);
                                    ingID++;
                                }
                            }
                            mListener.onSaveButtonClick(v, item, ingredientsList);
                            onDestroy();
                        }else {
                            mListener.onSaveButtonClick(v, item, null);
                            onDestroy();
                        }
                    }
                })
                .setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDeleteButtonClick(v, item);
                        onDestroy();                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (InfoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void setSelections(String item, List<Spinner> spinners, List<String> allIngredients, View view){
        List<Ingredient> usingIngredients = mListener.getItems(item);
        List<Integer> resValues = new ArrayList<>();
        resValues.add(R.id.value1);
        resValues.add(R.id.value2);
        resValues.add(R.id.value3);
        resValues.add(R.id.value4);
        resValues.add(R.id.value5);
        resValues.add(R.id.value6);

        for(int i = 0; i<usingIngredients.size();i++){
            spinners.get(i).setSelection(allIngredients.indexOf(usingIngredients.get(i).name));
            EditText valueEdit = view.findViewById(resValues.get(i));
            valueEdit.setText(String.valueOf(usingIngredients.get(i).value));
        }
    }

    public Spinner setSpinner(Spinner s, List<String> list){
        Spinner spinner = s;

        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
        return spinner;
    }

    public List<Integer> getListOfValues(View v){
        List<Integer> list= new ArrayList<>();
        List<String> strings = new ArrayList<>();

        strings.add(((EditText) v.findViewById(R.id.value1)).getText().toString());
        strings.add(((EditText) v.findViewById(R.id.value2)).getText().toString());
        strings.add(((EditText) v.findViewById(R.id.value3)).getText().toString());
        strings.add(((EditText) v.findViewById(R.id.value4)).getText().toString());
        strings.add(((EditText) v.findViewById(R.id.value5)).getText().toString());
        strings.add(((EditText) v.findViewById(R.id.value6)).getText().toString());

        for(int i=0;i<strings.size();i++){
            if(strings.get(i).equals(""))list.add(0);
            else list.add(Integer.parseInt(strings.get(i)));
        }
        return list;
    }

}






