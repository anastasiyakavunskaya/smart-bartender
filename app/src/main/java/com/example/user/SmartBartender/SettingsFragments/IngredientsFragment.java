package com.example.user.SmartBartender.SettingsFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.SmartBartender.EditModel;
import com.example.user.SmartBartender.ItemAdapter;
import com.example.user.SmartBartender.ItemModel;
import com.example.user.SmartBartender.ItemPresenter;
import com.example.user.SmartBartender.R;
import com.example.user.SmartBartender.Settings;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IngredientsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IngredientsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class IngredientsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ItemAdapter adapter;
    private OnFragmentInteractionListener mListener;
    String editName;
    View view;
    public IngredientsFragment() {}


    // TODO: Rename and change types and number of parameters
    public static IngredientsFragment newInstance(String param1, String param2) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        RecyclerView recycler = view.findViewById(R.id.ing_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final EditText editText = view.findViewById(R.id.edit_ingredient);
        final SettingsPresenter presenter = new SettingsPresenter(new EditModel(getContext()));

        adapter = new ItemAdapter(true, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                editText.setText(item);
                editName = item;
            }
            @Override
            public void onButtonClick(String item) {
                presenter.deleteItemFromList(item);
                presenter.fragmentIsReady();
                editName = null;
                editText.setText("");
            }
        });
        recycler.setAdapter(adapter);
        presenter.attachFragment(this);
        presenter.fragmentIsReady();
        Button saveButton = view.findViewById(R.id.save_btn);
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
