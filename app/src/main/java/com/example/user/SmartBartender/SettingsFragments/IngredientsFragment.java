package com.example.user.SmartBartender.SettingsFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.SmartBartender.EditModel;
import com.example.user.SmartBartender.R;
import com.example.user.SmartBartender.SmartBartender;


import java.util.ArrayList;


public class IngredientsFragment extends Fragment {

    private IngredientsAdapter adapter;
    private OnFragmentInteractionListener mListener;
    String editName;
    View view;
    EditText editText;
    Button saveButton;
    SmartBartender settings;
    public IngredientsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        RecyclerView recycler = view.findViewById(R.id.ing_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        saveButton = view.findViewById(R.id.save_btn);
        editText = view.findViewById(R.id.edit_ingredient);
        final SettingsPresenter presenter = new SettingsPresenter(new EditModel(getContext()));
        settings = (SmartBartender)getActivity().getApplication();
        adapter = new IngredientsAdapter(new IngredientsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                editText.setText(item);
                editName = item;
            }
            @Override
            public void onButtonClick(String item) {
                settings.deleteItemFromSettings(presenter.getIngredientId(item));
                presenter.deleteItemFromList(item);
                presenter.fragmentIsReady();
                editName = null;
                editText.setText("");
            }
        });
        recycler.setAdapter(adapter);
        presenter.attachFragment(this);
        presenter.fragmentIsReady();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    presenter.saveItemToList(editName, editText.getText().toString());
                    presenter.fragmentIsReady();
                    editName = null;
                    editText.setText("");
            }
        });
        return view;
    }

    public void showItems(ArrayList<String> list){
        adapter.setData(list);
    }
    public void showText(String text){
        TextView textView = view.findViewById(R.id.ingredients_warning_text);
        textView.setText(text);
    }
    public void showToast(String text){
        Toast.makeText(getContext(),text,Toast.LENGTH_LONG).show();
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
