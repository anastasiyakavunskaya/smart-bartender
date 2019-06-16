package com.example.user.SmartBartender.SettingsFragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.user.SmartBartender.DatabaseHelper;
import com.example.user.SmartBartender.EditModel;
import com.example.user.SmartBartender.R;
import com.example.user.SmartBartender.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MotorsSettingsFragment extends Fragment {

    private final int NUMBER_OF_INGREDIENTS = 6;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface OnFragmentInteractionListener {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Spinner setSpinner(Spinner s, List<String> list){

        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>((Objects.requireNonNull(getContext())), android.R.layout.simple_spinner_item, list);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        EditModel model = new EditModel(getContext());
        final SettingsPresenter presenter = new SettingsPresenter(model);
        View view;
        if(presenter.getAllIngredients().isEmpty()){
            view = inflater.inflate(R.layout.blocked_fragment, null);
        }
        else {
            view = inflater.inflate(R.layout.fragment_motors_settings, null);
            init(view, presenter);
        }



        return view;
    }
    void init(View v, final SettingsPresenter presenter){
        final List<Spinner> spinners = new ArrayList<>();
        final ArrayList<String> ingredientsList = presenter.getAllIngredients();
        final ArrayList<Integer> settings = presenter.getSettingIngredientsId();
        ingredientsList.add(0, "Пусто");

        spinners.add(setSpinner((Spinner) v.findViewById(R.id.setting_1), ingredientsList));
        spinners.add(setSpinner((Spinner) v.findViewById(R.id.setting_2), ingredientsList));
        spinners.add(setSpinner((Spinner) v.findViewById(R.id.setting_3), ingredientsList));
        spinners.add(setSpinner((Spinner) v.findViewById(R.id.setting_4), ingredientsList));
        spinners.add(setSpinner((Spinner) v.findViewById(R.id.setting_5), ingredientsList));
        spinners.add(setSpinner((Spinner) v.findViewById(R.id.setting_6), ingredientsList));

        for (int i = 0; i < NUMBER_OF_INGREDIENTS; i++) {
            if ((settings.get(i)) != -1) spinners.get(i).setSelection(settings.get(i));
        }

        Button clearButton, setButton;
        clearButton = v.findViewById(R.id.clear_motors_btn);
        setButton = v.findViewById(R.id.set_motors_btn);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.deleteSettings();
                onDestroy();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < NUMBER_OF_INGREDIENTS; i++) {
                    Object ing = spinners.get(i).getSelectedItem();
                    presenter.setSettings(ing.toString(), false, i);
                }
            }
        });
    }

}
