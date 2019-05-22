package com.example.user.SmartBartender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener, SettingsFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        Button ingredientsBtn = findViewById(R.id.ingredients_button);
        ingredientsBtn.setOnClickListener(this);

        Button recipesBtn = findViewById(R.id.recipes_button);
        recipesBtn.setOnClickListener(this);

        Button settingsBtn = findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(this);

        Button infoBtn = findViewById(R.id.info_button);
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

}
