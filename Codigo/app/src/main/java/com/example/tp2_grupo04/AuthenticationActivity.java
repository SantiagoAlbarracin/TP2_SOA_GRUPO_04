package com.example.tp2_grupo04;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;


public class AuthenticationActivity extends AppCompatActivity {

    private Button btnAccept;
    private Button btnCancel;
    private Button btnSendCode;
    private EditText userCode;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        btnAccept = (Button) findViewById(R.id.btnAcceptAuth);
        btnCancel = (Button) findViewById(R.id.btnCancelAuth);
        btnSendCode = (Button) findViewById(R.id.btnSendCodeAuth);
        userCode = (EditText) findViewById(R.id.editTextCode);


        Integer random = (int) (Math.random() * 1000);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userMail = extras.getString("loginEmail");

        String enviarasunto = "Codigo de verificacion";
        String enviarmensaje = "Su codigo de verificacion es:"+random.toString();

        /*JavaMailAPI javaMailApi = new JavaMailAPI(this,userMail,enviarasunto,enviarmensaje);
        javaMailApi.execute();*/

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                sendSMS();
            }else{
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
            }
        }



    }

    private void sendSMS(){
        String number="1122406872";
        String message="Hola";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(this, "El mensaje se envio papa", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "El mensaje no se pudo enviar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}