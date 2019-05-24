package com.example.user.SmartBartender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_NAME;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_RECIPES_ID;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_ING_REC;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_RECIPES;

class ItemModel {
    private final DatabaseHelper dbHelper;
    private ItemPresenter presenter;
    private ConnectedThread mConnectedThread;



    private static String address = "00:15:FF:F2:19:4C";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private BluetoothAdapter mBluetoothAdapter = null;

    private static final int REQUEST_ENABLE_BT = 1;

    /**
     * Member object for the chat services
     */
    private BluetoothSocket mSocket = null;


    ItemModel(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    ArrayList<String> loadItems() {
        String warning = "Пусто, добавьте первый ингредиент";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_INGREDIENTS, null, null, null, null, null, KEY_NAME);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        } else
            list.add(warning);
        cursor.close();
        return list;
    }

    private boolean isReadyToCook(String recipeName) {
        EditModel model = new EditModel(dbHelper);
        ArrayList<Integer> settings = model.getSettingsDbIds();
        ArrayList<Integer> recipe = getRecipesIds(recipeName);
        boolean ingredientInList = false;
        for(int i=0;i<recipe.size();i++){
            for(int j=0;j<settings.size();j++){
                if (recipe.get(i).equals(settings.get(j))){
                    ingredientInList = true;
                }
            }
            if(!ingredientInList)return false;
            ingredientInList = false;
        }
        return  true;
    }

    private ArrayList<Integer> getRecipesIds(String item) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item + "\"";
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
            int recId = c.getInt(c.getColumnIndex(KEY_ID));
            String selectId = "SELECT  * FROM " + TABLE_ING_REC + " WHERE " + KEY_RECIPES_ID + " = " + "\"" + recId + "\"";

            Cursor c1 = database.rawQuery(selectId, null);
            if ((c1 != null) && (c1.moveToFirst())) {
                do {
                    int id = c1.getInt((c1.getColumnIndex(KEY_INGREDIENTS_ID)));
                    list.add(id);
                } while (c1.moveToNext());
                c1.close();
            }
            c.close();
        }
        return list;
    }

    ArrayList<String> loadItemsReadyToCook(){
        String warning = "Пусто, добавьте первый ингредиент";
        ArrayList<String> readyToCookRecipes = new ArrayList<>();
        for(int i=0;i<allRecipes().size();i++){
            if(isReadyToCook(allRecipes().get(i))) readyToCookRecipes.add(allRecipes().get(i));
        }
        if(readyToCookRecipes.isEmpty())readyToCookRecipes.add(warning);

        return readyToCookRecipes;
    }

    private ArrayList<String> allRecipes(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_RECIPES, null, null, null, null, null, KEY_NAME);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    void onCookClick(String item){
        presenter.showError("Запрос отправлен");
        presenter.showError("Приготовление "+item+" началось");

        //int[] recipe = getRecipe(item);
        //String output = generateOutputString(recipe);
        //sendData("200;100;0;0;0;0;");
        //mConnectedThread.write("200;100;0;0;0;0");

    }

    private int[] getRecipe(String item){
         EditModel model = new EditModel(dbHelper);
         List<Ingredient> ingredients = model.getItems(item);
         ArrayList<Integer> settings = model.getSettingsDbIds();
         //Список для последовательноой записи ингредиентов с их объемом
         int[] recipe = new int[10];
         for(int i =0; i<ingredients.size();i++){
             for (int j=0; j<settings.size();j++){
                 if(ingredients.get(i).id == settings.get(j))recipe[j]=ingredients.get(i).value*model.getCoefficient();
             }
         }
         return recipe;
     }

     private String generateOutputString(int[] recipe){
        String str=" ";
         for (int i1 : recipe) {
             str = +i1 + ";";
         }
        return str;
     }

     void attach(ItemPresenter presenter){
        this.presenter = presenter;
     }

    void bluetoothOnResume(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "...onResume - попытка соединения...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            presenter.showError( "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Соединяемся...");
        try {
            mSocket.connect();
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e2) {
                presenter.showError("In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Создание Socket...");

        mConnectedThread = new ConnectedThread(mSocket);
        mConnectedThread.start();
    }

   /*void checkBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(mBluetoothAdapter==null) {
            presenter.showError( "Bluetooth не поддерживается");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth включен...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                presenter.startActivity(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }*/

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { }
            mmOutStream = tmpOut;
        }

        /* Call this from the main activity to send data to the remote device */
        void write(String message) {
            Log.d(TAG, "...Данные для отправки: " + message + "  ");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "  ");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}

