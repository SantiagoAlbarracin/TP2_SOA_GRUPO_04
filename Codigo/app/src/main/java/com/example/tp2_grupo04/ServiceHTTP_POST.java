package com.example.tp2_grupo04;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceHTTP_POST extends IntentService {

    URL url;
    HttpURLConnection connection = null;

    public ServiceHTTP_POST() {
        super("ServiceHTTP_POST");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
    }

    protected void onHandleIntent(Intent intent) {
        try {
            String uri = intent.getExtras().getString("uri");
            JSONObject jsonObject = new JSONObject(intent.getExtras().getString("dataJSON"));
            executePost(uri, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String POST(String uri, JSONObject jsonObject) {
        String result = "";
        try {
            url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(jsonObject.toString().getBytes("UTF-8"));
            dataOutputStream.flush();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                result = Utils.convertInputStreamToString(inputStreamReader).toString();
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                result = Utils.convertInputStreamToString(inputStreamReader).toString();
            } else {
                result = "NOT_OK";
            }
            dataOutputStream.close();
            connection.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void executePost(String uri, JSONObject jsonObject) {
        String result = POST(uri, jsonObject);
        if (result == null) {
            return;
        }
        if (result == "NOT_OK") {
            return;
        }
        Intent intent = new Intent("com.example.httoconnection_intentservice.intent.action.RESPUESTA_OPERACION");
        intent.putExtra("dataJSON", result);
        sendBroadcast(intent);
    }
}
