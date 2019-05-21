package com.example.user.SmartBartender;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends AppCompatDialogFragment {

    int NUMBER_OF_INGREDIENTS = 6;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.fragment_settings,null);

        EditModel model = new EditModel(new DatabaseHelper(getContext()));
        final SettingsPresenter presenter = new SettingsPresenter(model);

        String title = "Установите жидкости";
        final List<Spinner> spinners = new ArrayList<>();

        final ArrayList<String> ingredientsList = presenter.getAllIngredients();
        final ArrayList<Integer> settings = presenter.getSettingIngredientsId();
        final EditText editCoefficient = v.findViewById(R.id.edit_coef);
        editCoefficient.setText(presenter.getCoefficient());
        ingredientsList.add(0,"Пусто");

            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_1),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_2),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_3),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_4),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_5),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_6),ingredientsList));

            for(int i = 0;i<NUMBER_OF_INGREDIENTS;i++){
                if((settings.get(i)) != -1) spinners.get(i).setSelection(settings.get(i));
            }

        builder.setView(v)
                .setTitle(title)
                .setPositiveButton("Установить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for(int i = 0;i<NUMBER_OF_INGREDIENTS;i++){
                            Object ing = spinners.get(i).getSelectedItem();
                            presenter.setSettings(ing.toString(),false, i);
                        }
                        presenter.setSettings(editCoefficient.getText().toString(),true, 0);
                    }
                })
                .setNegativeButton("Очистить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.deleteSettings();
                        onDestroy();
                        }
});
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            //mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public Spinner setSpinner(Spinner s, List<String> list){

        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>((getContext()), android.R.layout.simple_spinner_item, list);
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

}
