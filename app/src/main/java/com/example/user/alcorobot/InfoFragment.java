package com.example.user.alcorobot;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class InfoFragment extends AppCompatDialogFragment{


    public interface InfoDialogListener {
        void onDeleteButtonClick(View view, String item);
        void onEditButtonClick(View view, String item);
    }
    InfoDialogListener  mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View v = inflater.inflate(R.layout.ingredients_add_dialog_layout,null);
        final String item = getArguments().getString("item");


        EditText editText = v.findViewById(R.id.ingredient_name);
        editText.setText(item);
        builder.setView(v)
                .setTitle("Изменение ингредиента")
                .setPositiveButton ("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onEditButtonClick(v, item);
                        onDestroy();
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

}
