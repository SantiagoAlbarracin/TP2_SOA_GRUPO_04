package com.example.tp2_grupo04;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventAsyncTask extends AsyncTask<String, Void, Boolean> {


    private MenuActivity menuActivity;
    private User user;
    private Boolean sendEvent=false;
    private Boolean internetConnection=false;

    public EventAsyncTask(MenuActivity menuActivity) {
        this.menuActivity = menuActivity;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        JSONObject answer = null;
        JSONObject object = new JSONObject();
        String result = null;
        if (!Utils.isInternetAvailable()) {
            internetConnection = false;
            return false;
        }
        try {
            internetConnection = true;
            object.put("env", "TEST");
            object.put("type_events", strings[0]);
            object.put("description", strings[1]);
            String token = strings[2];

            URL url = new URL(Utils.URI_REGISTER_EVENT);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(object.toString().getBytes("UTF-8"));

            Log.i("debug555", "Se envia al servidor " + object.toString());
            Log.i("debug555", "Se envia token " + token);


            dataOutputStream.flush();

            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                result = Utils.convertInputStreamToString(inputStreamReader).toString();
                sendEvent = true;

            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                result = Utils.convertInputStreamToString(inputStreamReader).toString();
                sendEvent = false;

            } else {
                result = "NOT_OK";
                sendEvent = false;

            }
            Log.i("debug555", "Me contestó " + result);

            dataOutputStream.close();
            connection.disconnect();

            answer = new JSONObject(result);

            result = answer.get("success").toString();


        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        Log.i("debug555", "Llegó: " + answer.toString());


        if (result.matches("true")) {
            return true;
        }

        return false;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onPostExecute(Boolean o) {
        if (o) {
            Log.i("debug555", "se envio piolita");
        } else {
            if (!sendEvent && internetConnection) {
                this.menuActivity.setAlertText("Error al Enviar!", "Intente nuevamente.");
            }
            if (!internetConnection){
                this.menuActivity.setAlertText("Error de conexion!", "Debe conectarse a internet e intentar nuevamente");
            }
        }
    }


}