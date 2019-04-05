package com.example.user.alcorobot;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MenuActivity extends Activity implements View.OnClickListener {

    Button ingButton, recipesButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        ingButton = findViewById(R.id.ingredients_button);
        ingButton.setOnClickListener(this);

        recipesButton = findViewById(R.id.recipes_button);
        recipesButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ingredients_button:
                Intent ingIntent = new Intent(this, IngredientsActivity.class);
                startActivity(ingIntent);
                break;
            case R.id.recipes_button:
                Intent recIntent = new Intent(this, RecipesActivity.class);
                startActivity(recIntent);
                break;
                default:
                    break;
        }

    }
}
