package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SetPINVerification extends AppCompatActivity {

    TextInputEditText enter_pin,confirm_pin;
    Button set_pin;

    private int ePIN = 0, cPIN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpinverification);
        setUI();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        set_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(enter_pin.getText().toString().isEmpty() && confirm_pin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter PIN to Proceed", Toast.LENGTH_SHORT).show();

                }
                else if(confirm_pin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Confirm PIN to Proceed", Toast.LENGTH_SHORT).show();

                }
                else{
                    ePIN = Integer.parseInt(enter_pin.getText().toString());
                    cPIN = Integer.parseInt(confirm_pin.getText().toString());
                    if (enter_pin.getText().toString().length() < 4 || enter_pin.getText().toString().length() > 6){
                        Toast.makeText(getApplicationContext(), "PIN less than 4 and greater than 6 digits not allowed", Toast.LENGTH_SHORT).show();
                    }
                    else if(ePIN != cPIN){
                        Toast.makeText(getApplicationContext(), "Enter the same PIN as above", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String PIN = enter_pin.getText().toString().trim();

                        try {
                            PIN = AESUtils.encrypt(PIN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        editor.putString(encrypt("PIN"),PIN);
                        editor.commit();
                        startActivity(new Intent(SetPINVerification.this,Vault.class));
                        finish();


                    }
                }

            }
        });

    }

    private void setUI() {
        enter_pin = (TextInputEditText) findViewById(R.id.enter_pin);
        confirm_pin = (TextInputEditText) findViewById(R.id.confirm_pin);
        set_pin = (Button) findViewById(R.id.set_button);
    }

    private static String encrypt(String input) {
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }


}