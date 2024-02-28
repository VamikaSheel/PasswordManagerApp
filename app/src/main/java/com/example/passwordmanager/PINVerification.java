package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PINVerification extends AppCompatActivity {

    TextInputEditText user_enter_PIN;
    TextView timer;
    Button submit;
    int attempt = (int)Math.floor(Math.random()*(8-4+1)+4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinverefication);
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        timer = (TextView) findViewById(R.id.acountdown);




     user_enter_PIN = (TextInputEditText) findViewById(R.id.user_enter_pin);
     submit = (Button) findViewById(R.id.next_button);
     submit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             String pass =  sharedPreferences1.getString(encrypt("PIN"), null);

             try {
                 pass = AESUtils.decrypt(pass);
             } catch (Exception e) {
                 e.printStackTrace();
             }

             int sPIN = Integer.parseInt(pass);



             if(attempt  >= 1){
                 if(user_enter_PIN.getText().toString().isEmpty()){
                     Toast.makeText(getApplicationContext(), "Enter PIN to proceed", Toast.LENGTH_SHORT).show();
                 }
                 else{
                     int ePass = Integer.parseInt(user_enter_PIN.getText().toString().trim());

                     if(sPIN != ePass){
                         Toast.makeText(getApplicationContext(), "Incorrect PIN entered", Toast.LENGTH_SHORT).show();
                         Toast.makeText(getApplicationContext(), "Attempt left " + attempt, Toast.LENGTH_SHORT).show();
                     }
                     else{
                         startActivity(new Intent(PINVerification.this,Vault.class));
                         finish();
                     }
                 }

             }
             else{
                 Toast.makeText(getApplicationContext(), "Attempt limit exceed", Toast.LENGTH_SHORT).show();
                 new CountDownTimer(60000, 1000) {

                     public void onTick(long millisUntilFinished) {
                         timer.setVisibility(View.VISIBLE);
                         submit.setVisibility(View.GONE);
                         timer.setText("try again after: " + millisUntilFinished / 1000 +" s");

                     }

                     public void onFinish() {
                         timer.setVisibility(View.GONE);
                         submit.setVisibility(View.VISIBLE);
                         attempt = (int)Math.floor(Math.random()*(6-4+1)+4);
                     }

                 }.start();

             }
             attempt -= 1;


         }
     });
    }
    private static String encrypt(String input) {
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

}







                                                      