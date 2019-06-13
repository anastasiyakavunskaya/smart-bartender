package com.example.user.SmartBartender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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
    private static ConnectedThread mConnectedThread;

    private static String address = "98:D3:34:90:A2:F1";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static BluetoothAdapter mBluetoothAdapter = null;
    static BluetoothSocket mSocket = null;


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
        String output;
        int[] recipe = getRecipe(item);
        boolean isLayer = isLayer(item);
        //TODO переделать генерацию строки для слоёных коктейлей
        if(isLayer) output = generateSimpleOutputString(recipe);
        else output = generateSimpleOutputString(recipe);
        if(mSocket!=null){
            mConnectedThread.write(output);
            presenter.showToast("Приготовление напитка началось");
        }
        else  {
            Log.d(TAG, "...Не удалось отправить...");
            presenter.showToast("Что-то пошло не так!");
        }
    }

    private int[] getRecipe(String item){
         EditModel model = new EditModel(dbHelper);
         List<Ingredient> ingredients = model.getItems(item);
         ArrayList<Integer> settings = model.getSettingsDbIds();
         //Список для последовательноой записи ингредиентов с их объемом
         int[] recipe = new int[6];
         for(int i =0; i<ingredients.size();i++){
             for (int j=0; j<settings.size()-1;j++){
                 if(ingredients.get(i).id == settings.get(j))recipe[j]=ingredients.get(i).value*model.getCoefficient();
             }
         }
         return recipe;
    }

    private String generateSimpleOutputString(int[] recipe){
        String output;
        StringBuilder str = new StringBuilder();
        for (int i = 0;i<recipe.length-1;i++) {
         str.append(i+1).append(",").append(recipe[i]).append(":");
        }
        output = str.toString() +recipe.length+","+recipe[recipe.length-1]+".";
        return output;
    }

    void attach(ItemPresenter presenter){
        this.presenter = presenter;
    }

    static class ConnectBluetooth extends AsyncTask<Void,Void,Void>{
        boolean ConnectSuccess = true;

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (mSocket == null)
                {
                   mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    Log.d(TAG, "...Соединяемся...");
                    try {
                        mSocket.connect();
                        Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
                    } catch (IOException e) {
                        try {
                           mSocket.close();
                        } catch (IOException e2) {
                            Log.d(TAG,  "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        }
                    }
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
                Log.d(TAG, "...Не удалось ничего.....  ");
            }

            mConnectedThread = new ConnectedThread(mSocket);
            mConnectedThread.start();
            return null;
        }
    }

    private static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {
                Log.d(TAG, "...Не удалось открыть поток.....  ");
            }
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

    boolean isLayer(String item){
        return true;
    }
}

