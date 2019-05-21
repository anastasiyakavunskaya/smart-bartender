package com.example.user.SmartBartender;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener, SettingsFragment.OnFragmentInteractionListener {

    Button ingredientsBtn, recipesBtn, settingsBtn, infoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        ingredientsBtn = findViewById(R.id.ingredients_button);
        ingredientsBtn.setOnClickListener(this);

        recipesBtn = findViewById(R.id.recipes_button);
        recipesBtn.setOnClickListener(this);

        settingsBtn = findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(this);

        infoBtn = findViewById(R.id.info_button);
        infoBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ingredients_button:
                Intent ingIntent = new Intent(this, ItemActivity.class);
                ingIntent.putExtra("isIngredient", true);
                startActivity(ingIntent);
                break;
            case R.id.recipes_button:
                Intent recIntent = new Intent(this, ItemActivity.class);
                recIntent.putExtra("isIngredient", false);
                startActivity(recIntent);
                break;
            case R.id.info_button:
                InfoFragment infoFragment = new InfoFragment();
                FragmentManager fm1 = getSupportFragmentManager();
                infoFragment.show(fm1,"information");
                break;
            case R.id.settings_button:
                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentManager fm2 = getSupportFragmentManager();
                settingsFragment.show(fm2,"settings");
                break;
                default:
                    break;        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
