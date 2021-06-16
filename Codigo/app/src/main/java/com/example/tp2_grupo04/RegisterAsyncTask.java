package com.example.tp2_grupo04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RegisterAsyncTask extends AsyncTask<String, Void, Boolean> {

    private RegisterActivity registerActivity;
    private User user;
    private Boolean registerSucces;
    private Boolean internetConnection;

    public RegisterAsyncTask(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        JSONObject object = new JSONObject();
        String result;
        if (!Utils.isInternetAvailable()) {
            internetConnection = false;
            AlertDialog alertDialog = new AlertDialog.Builder(this.registerActivity).create();
            alertDialog.setTitle("Error de conexion");
            alertDialog.setMessage("Debe conectarse a internet e intentar nuevamente");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return false;
        }
        internetConnection = true;

        try {
            object.put("env", "TEST");
            object.put("name", strings[0]);
            object.put("lastname", strings[1]);
            object.put("dni", Integer.valueOf(strings[2]));
            object.put("email", strings[3]);
            object.put("password", strings[4]);
            object.put("commission", Integer.valueOf(strings[5]));
            object.put("group", Integer.valueOf(strings[6]));

            URL url = new URL(Utils.URI_REGISTER_USER);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(object.toString().getBytes("UTF-8"));


            Log.i("debug104", "Se envia al servidor " + object.toString());

            dataOutputStream.flush();

            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                result = Utils.convertInputStreamToString(inputStreamReader).toString();
                registerSucces = true;

            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                result = Utils.convertInputStreamToString(inputStreamReader).toString();
                registerSucces = false;

            } else {

                result = "NOT_OK";
                registerSucces = false;
            }

            Log.i("debug83", "Me contestó " + result);
            dataOutputStream.close();
            connection.disconnect();

            JSONObject answer = new JSONObject(result);

            result = answer.get("success").toString();


            if (result.matches("true")) {
                Log.i("debug166", answer.toString());
                return true;
            }

            return false;
        } catch (JSONException | MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;


    }

    @Override
    protected void onPreExecute() {
        this.registerActivity.progressBar.setVisibility(View.VISIBLE);
        this.registerActivity.btnAccept.setEnabled(false);
        this.registerActivity.btnCancel.setEnabled(false);
    }


    @Override
    protected void onPostExecute(Boolean o) {

        if (o) {
            this.registerActivity.lanzarActivity();
        } else {
            if (!registerSucces && internetConnection) {
                this.registerActivity.setAlertText("Error de Registro!", "Error al registrar, intente nuevamente.");
            }
            this.registerActivity.progressBar.setVisibility(View.INVISIBLE);
            this.registerActivity.btnAccept.setEnabled(true);
            this.registerActivity.btnCancel.setEnabled(true);
        }
    }
}
