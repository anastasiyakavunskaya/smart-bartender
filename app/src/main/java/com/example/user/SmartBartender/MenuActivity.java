package com.example.user.SmartBartender;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import static android.content.ContentValues.TAG;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener, SettingsFragment.OnFragmentInteractionListener {

    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        Button recipesBtn = findViewById(R.id.recipes_button);
        recipesBtn.setOnClickListener(this);

        Button settingsBtn = findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(this);


        checkBluetooth();
        while(ItemModel.mSocket==null){
            ItemModel.ConnectBluetooth bluetoothConnection = new ItemModel.ConnectBluetooth();
            bluetoothConnection.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recipes_button:
              /*  Intent recIntent = new Intent(this, ItemActivity.class);
                recIntent.putExtra("isIngredient", false);
                startActivity(recIntent);*/
                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                FragmentManager fm = getSupportFragmentManager();
                //ingredientsFragment.onCreate();
                break;
            case R.id.settings_button:
                Intent setIntent = new Intent(this, SettingsActivity.class);
                setIntent.putExtra("isIngredient", false);
                startActivity(setIntent);
                break;
                default:
                    break;
        }

    }
    void checkBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(mBluetoothAdapter==null) {
            Toast.makeText( this,"Bluetooth не поддерживается",Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth включен...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    }
}
