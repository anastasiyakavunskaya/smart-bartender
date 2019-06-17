package com.example.user.SmartBartender.SettingsFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.user.SmartBartender.R;
import com.example.user.SmartBartender.SmartBartender;


public class CoefficientFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private OnFragmentInteractionListener mListener;
    SmartBartender settings;
    TextView resultView;
    public CoefficientFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settings = (SmartBartender) getActivity().getApplication();
        View view =  inflater.inflate(R.layout.fragment_coefficient, container, false);
        resultView = view.findViewById(R.id.text_c);
        int i = settings.getCoefficient();
        resultView.setText(String.valueOf(i));

        final SeekBar seekBar = view.findViewById(R.id.seekBar_c);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(i);
        Button setButton = view.findViewById(R.id.save_c_btn);
        Button dropButton = view.findViewById(R.id.c_btn);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setCoefficient(Integer.parseInt(resultView.getText().toString()));
                int i = settings.getCoefficient();
                resultView.setText(String.valueOf(i));
        }
        });
        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setCoefficient(1500);
                int i = settings.getCoefficient();
                seekBar.setProgress(i);
                resultView.setText(String.valueOf(i));
            }
        });


        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        resultView.setText(String.valueOf(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        resultView.setText(String.valueOf(seekBar.getProgress()));

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        resultView.setText(String.valueOf(seekBar.getProgress()));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
