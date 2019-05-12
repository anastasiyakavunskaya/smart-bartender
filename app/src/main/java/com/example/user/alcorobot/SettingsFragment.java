package com.example.user.alcorobot;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import java.util.Objects;

import static com.example.user.alcorobot.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.alcorobot.DatabaseHelper.TABLE_SETTINGS;

public class SettingsFragment extends AppCompatDialogFragment {

    private OnFragmentInteractionListener mListener;
    DatabaseHelper dbHandler;

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<Spinner> spinners = new ArrayList<>();
        String title = "Установите жидкости";
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.fragment_settings,null);

        dbHandler = new DatabaseHelper(getContext());
        //получаем актуальную версию базы данных
        final SQLiteDatabase database = dbHandler.getReadableDatabase();

        assert getArguments() != null;
        final ArrayList<String> ingredientsList = getArguments().getStringArrayList("allIngredients");
        final ArrayList<Integer> settings = getArguments().getIntegerArrayList("settingIngredients");
        final int coefficient = getArguments().getInt("c");
        assert ingredientsList != null;
        ingredientsList.add(0,"Пусто");
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_1),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_2),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_3),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_4),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_5),ingredientsList));
            spinners.add(setSpinner((Spinner)v.findViewById(R.id.setting_6),ingredientsList));

            for(int i = 0;i<6;i++){
                assert settings != null;
                int s = settings.get(i);
                if(s != -1) spinners.get(i).setSelection(settings.get(i));
            }

           final EditText editText = v.findViewById(R.id.edit_coef);
            editText.setText(String.valueOf(coefficient));

        builder.setView(v)
                .setTitle(title)
                .setPositiveButton("Установить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues contentValues = new ContentValues();
                        for(int i = 0;i<6;i++){
                            Object ing = spinners.get(i).getSelectedItem();

                            if(ing.toString().equals("Пусто"))
                                contentValues.put(KEY_INGREDIENTS_ID,-1);
                            else
                                contentValues.put(KEY_INGREDIENTS_ID,ingredientsList.indexOf(ing.toString()));
                            database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(i+1)});
                            contentValues.clear();
                        }
                        int c = Integer.parseInt(editText.getText().toString());
                        contentValues.put(KEY_INGREDIENTS_ID,c);
                        database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(7)});
                        contentValues.clear();
                    }
                })
                .setNegativeButton("Очистить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(KEY_INGREDIENTS_ID,-1);
                        for (int i=1;i<7;i++){
                            database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(i)});
                        }
                        contentValues.clear();
                        contentValues.put(KEY_INGREDIENTS_ID,1);
                        database.update(TABLE_SETTINGS,contentValues,"_id = ?", new String [] {String.valueOf(7)});
                        contentValues.clear();
                        onDestroy();
                        }
});
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
