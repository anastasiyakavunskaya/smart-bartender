package com.example.user.alcorobot;

import android.app.Activity;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddDialogFragment extends AppCompatDialogFragment {

    public interface AddDialogListener {
         void onAddButtonClick(View view, List<Ingredient> i);
    }
    AddDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final int resource = getArguments().getInt("resource");
        final boolean isIngredient = getArguments().getBoolean("isIngredient");


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(resource,null);
        final List<Spinner> spinners = new ArrayList<>();
        if(!isIngredient){
            ArrayList<String> ingredients = getArguments().getStringArrayList("ingredients");
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing1),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing2),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing3),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing4),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing5),ingredients));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.ing6),ingredients));
        }

         builder.setView(v)
                 .setTitle(title)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
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
                            mListener.onAddButtonClick(v, ingredientsList);
                            onDestroy();
                        }else {
                            mListener.onAddButtonClick(v, null);
                            onDestroy();
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { onDestroy();}
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

    }
    public Spinner setSpinner(Spinner s, List<String> list){
        Spinner spinner = s;

        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select!");

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




