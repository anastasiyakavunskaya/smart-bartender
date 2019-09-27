package com.example.user.SmartBartender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_INGREDIENTS_ID;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_LAYER;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_NAME;
import static com.example.user.SmartBartender.DatabaseHelper.KEY_RECIPES_ID;

import static com.example.user.SmartBartender.DatabaseHelper.KEY_VOLUME;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_INGREDIENTS;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_ING_REC;
import static com.example.user.SmartBartender.DatabaseHelper.TABLE_RECIPES;

class ItemModel {
    private final DatabaseHelper dbHelper;
    private static ItemPresenter presenter;
    private static ConnectedThread mConnectedThread;
    static BluetoothSocket mSocket = null;
    private static String address = "98:D3:34:90:A2:F1";

    private static BluetoothAdapter mBluetoothAdapter = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ItemModel(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    boolean listIsEmpty(boolean layerRecipes){
        if(layerRecipes) return loadLayerRecipes().isEmpty();
        else return loadSimpleRecipes().isEmpty();
    }

    ArrayList<Pair<String,Integer>> loadSimpleRecipes() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Pair<String,Integer>> output = new ArrayList<>();

        Cursor cursor = database.query(TABLE_RECIPES, null, "type = ?", new String[] { "0" }, null, null, KEY_NAME);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();

        for(int i=0;i<list.size();i++){
            if(isReadyToCook(list.get(i))) output.add(new Pair<>(list.get(i),1));
            else output.add(new Pair<>(list.get(i),0));
        }

        return output;
    }

    ArrayList<Pair<String,Integer>> loadLayerRecipes() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Pair<String,Integer>> output = new ArrayList<>();

        Cursor cursor = database.query(TABLE_RECIPES, null, "type = ?", new String[] { "1" }, null, null, KEY_NAME);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            do {
                list.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();

        for(int i=0;i<list.size();i++){
            if(isReadyToCook(list.get(i))) output.add(new Pair<>(list.get(i),1));
            else output.add(new Pair<>(list.get(i),0));
        }

        return output;
    }

    private boolean isReadyToCook(String item) {
        ArrayList<Integer> recipe = getRecipesIds(item);
        ArrayList<Integer> settings = getMotorsIds();
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

   void onCookClick(String item, boolean isLayer){
        String output;
        if(isLayer){
            ArrayList<int[]> recipe = getLayerRecipe(item);
            output = generateLayerOutputString(recipe);
        }
        else {
            int[] recipe = getRecipe(item);
            output = generateSimpleOutputString(recipe);
        }
        if(mSocket!=null){
            mConnectedThread.write(output);
        }
        else  {
            presenter.showToast("Что-то пошло не так!");
        }
    }

   private String generateLayerOutputString(ArrayList<int[]> recipe) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<recipe.size();i++){
            stringBuilder.append(generateSimpleOutputString(recipe.get(i)));
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            stringBuilder.append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.append(".");
        return stringBuilder.toString();
   }
   private ArrayList<int[]> getLayerRecipe(String item) {
       int[] recipe;
       ArrayList<int[]> output = new ArrayList<>();
       List<Ingredient> usingIngredients = getItems(item);
       ArrayList<Integer> settings = getMotorsIds();
       ArrayList<Ingredient> firstLayer = new ArrayList<>(),secondLayer = new ArrayList<>(),thirdLayer = new ArrayList<>();
       int i;
       for (i = 0; i < usingIngredients.size(); i++) {
           switch (usingIngredients.get(i).layer){
               case 1:
                   firstLayer.add(usingIngredients.get(i));
                   break;
               case 2:
                   secondLayer.add(usingIngredients.get(i));
                   break;
               case 3:
                   thirdLayer.add(usingIngredients.get(i));
                   break;
           }
       }
       recipe = new int[6];
       for(i =0; i<firstLayer.size();i++){
           for (int j=0; j<settings.size()-1;j++){
               if(firstLayer.get(i).id == settings.get(j))recipe[j]=firstLayer.get(i).value*getCoefficient();
           }
       }
       output.add(recipe);
       recipe = new int[6];
       for(i =0; i<secondLayer.size();i++){
           for (int j=0; j<settings.size()-1;j++){
               if(secondLayer.get(i).id == settings.get(j))recipe[j]=secondLayer.get(i).value*getCoefficient();
           }
       }
       output.add(recipe);
       recipe = new int[6];
       for(i =0; i<thirdLayer.size();i++){
           for (int j=0; j<settings.size()-1;j++){
               if(thirdLayer.get(i).id == settings.get(j))recipe[j]=thirdLayer.get(i).value*getCoefficient();
           }
       }
       output.add(recipe);
       return output;
    }
    private int[] getRecipe(String item) {
        List<Ingredient> ingredients = getItems(item);
        ArrayList<Integer> settings = getMotorsIds();
        //Список для последовательноой записи ингредиентов с их объемом
        int[] recipe = new int[6];
        for(int i =0; i<ingredients.size();i++){
            for (int j=0; j<settings.size();j++){
                if(ingredients.get(i).id == settings.get(j))recipe[j]=ingredients.get(i).value*getCoefficient();
            }
        }
        return recipe;
    }
    private int getCoefficient() {
        SmartBartender settings = presenter.getSettings();
        return settings.getCoefficient();
    }
    private List<Ingredient> getItems(String item) {

        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT  * FROM " + TABLE_RECIPES + " WHERE " + KEY_NAME + " = " + "\"" + item +"\"";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
            int recId = c.getInt(c.getColumnIndex(KEY_ID));
            String selectId = "SELECT  * FROM " + TABLE_ING_REC + " WHERE " + KEY_RECIPES_ID + " = " + "\"" + recId + "\"";
            c.close();
            Cursor c1 = db.rawQuery(selectId, null);
            if ((c1 != null)&&(c1.moveToFirst())) {
                do {
                    int id = c1.getInt((c1.getColumnIndex(KEY_INGREDIENTS_ID)));
                    int value = c1.getInt((c1.getColumnIndex(KEY_VOLUME)));
                    int layer = c1.getInt((c1.getColumnIndex(KEY_LAYER)));

                    String selectName = "SELECT  * FROM " + TABLE_INGREDIENTS + " WHERE " + KEY_ID + " = " + "\""+ id + "\"";
                    Cursor c2 = db.rawQuery(selectName, null);
                    if((c2!=null)&&(c2.moveToFirst())) {
                        do {
                            int index = (c2.getColumnIndex(KEY_NAME));
                            String name = c2.getString(index);
                            Ingredient ing = new Ingredient(value, name, id, layer);
                            ingredients.add(ing);
                        } while (c2.moveToNext());
                        c2.close();
                    }
                } while (c1.moveToNext());
                c1.close();
            }
        }
        return ingredients;
    }
    private String generateSimpleOutputString(int[] recipe){
        StringBuilder str = new StringBuilder();
        for (int i = 0;i<recipe.length-1;i++) {
            if(recipe[i]!=0) str.append(i+1).append(",").append(recipe[i]).append(":");
        }
        return str.toString() +recipe.length+","+recipe[recipe.length-1]+".";
   }
   void attach(ItemPresenter presenter){
        this.presenter = presenter;
    }
    private ArrayList<Integer> getMotorsIds(){
        SmartBartender settings = presenter.getSettings();
        return settings.getMotors();
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
                //ConnectSuccess = false;//if the try failed, you can check the exception here
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
            } catch (IOException ignored) { }
        }
    }
}

