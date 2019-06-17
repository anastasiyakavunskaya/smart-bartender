package com.example.user.SmartBartender.SettingsFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.user.SmartBartender.EditModel;
import com.example.user.SmartBartender.R;
import com.example.user.SmartBartender.SmartBartender;

import java.util.ArrayList;
import java.util.List;


public class MotorsSettingsFragment extends Fragment {

    ArrayList<Integer> motors;
    int coefficient, numberOfMotors;
    SmartBartender settings;
    View view;

    @NonNull
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        EditModel model = new EditModel(getContext());
        final SettingsPresenter presenter = new SettingsPresenter(model);
        settings = (SmartBartender) getActivity().getApplication();
        motors = settings.getMotors();
        coefficient = settings.getCoefficient();
        numberOfMotors = settings.getNumberOfMotors();
        if(presenter.getAllIngredients().isEmpty()){
            view = inflater.inflate(R.layout.blocked_fragment, null);
        }
        else {
            final List<Spinner> spinners = new ArrayList<>();
            view = inflater.inflate(R.layout.fragment_motors_settings, null);
            initView(presenter, spinners);
        }
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface OnFragmentInteractionListener {
    }


    void initView(final SettingsPresenter presenter, final List<Spinner> spinners){
        final ArrayList<String> ingredientsList = presenter.getAllIngredients();
        ingredientsList.add(0, "Пусто");

        initSpinners(ingredientsList, spinners);
        updateSpinners(ingredientsList,spinners,presenter);
        Button clearButton, setButton;
        clearButton = view.findViewById(R.id.clear_motors_btn);
        setButton = view.findViewById(R.id.set_motors_btn);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.deleteSettings();
                updateSpinners(ingredientsList,spinners,presenter);
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> update = new ArrayList<>();
                for (int j = 0; j < numberOfMotors; j++) {
                    int id = presenter.getIngredientId((spinners.get(j).getSelectedItem().toString()));
                    if(id!=0) update.add(j,id);
                    else update.add(j,0);
                }
                settings.setMotors(update);
                updateSpinners(ingredientsList,spinners,presenter);
            }
        });
    }

    public void initSpinners(ArrayList<String> ingredientsList, List<Spinner> spinners){
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.setting_1), ingredientsList));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.setting_2), ingredientsList));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.setting_3), ingredientsList));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.setting_4), ingredientsList));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.setting_5), ingredientsList));
        spinners.add(setSpinner((Spinner) view.findViewById(R.id.setting_6), ingredientsList));
    }

    private Spinner setSpinner(Spinner s, List<String> list){

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

    public void updateSpinners(ArrayList<String> ingredientsList, List<Spinner> spinners, SettingsPresenter presenter){
        for (int i = 0; i < numberOfMotors ; i++) {
            motors = settings.getMotors();
            spinners.get(i).setSelection(ingredientsList.indexOf(presenter.getIngredientById(motors.get(i))));
        }
    }

}
