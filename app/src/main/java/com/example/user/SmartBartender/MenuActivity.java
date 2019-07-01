package com.example.user.SmartBartender;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.SmartBartender.SettingsFragments.MotorsSettingsFragment;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener, MotorsSettingsFragment.OnFragmentInteractionListener {

    private static final int REQUEST_ENABLE_BT = 1;
    Button recipesBtn, recipesLayerBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        recipesBtn = findViewById(R.id.recipes_button);
        recipesBtn.setOnClickListener(this);

        recipesLayerBtn = findViewById(R.id.layer_recipes_button);
        recipesLayerBtn.setOnClickListener(this);

        settingsBtn = findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(this);

        checkBluetooth();
        ItemModel.ConnectBluetooth bluetoothConnection = new ItemModel.ConnectBluetooth();
        bluetoothConnection.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recipes_button:
                Intent recSimpleIntent = new Intent(this, ItemActivity.class);
                recSimpleIntent.putExtra("layerRecipes", false);
                startActivity(recSimpleIntent);
                break;
            case R.id.layer_recipes_button:
                Intent recLayerIntent = new Intent(this, ItemActivity.class);
                recLayerIntent.putExtra("layerRecipes", true);
                startActivity(recLayerIntent);
                break;
            case R.id.settings_button:
                Intent setIntent = new Intent(this, SettingsActivity.class);
                startActivity(setIntent);
                break;
                default:
                    break;
        }

    }
    void checkBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter ==null) {
            Toast.makeText( this,"Bluetooth не поддерживается",Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                //Toast.makeText( this,"Bluetooth включен",Toast.LENGTH_SHORT).show();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    }
}
