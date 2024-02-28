package com.example.passwordmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Vault extends AppCompatActivity {
    TextView t1,t2;
    FloatingActionButton add_new;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<UserData> userDataList;
    Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        t1 = (TextView)findViewById(R.id.textView4);
        t2 = (TextView)findViewById(R.id.textView8);
        add_new = (FloatingActionButton) findViewById(R.id.add_more);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        userDataList = new ArrayList<>();


        loadData();
        recycler();




        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Vault.this);
                View view1 = getLayoutInflater().inflate(R.layout.custom_dialog,null);




                alert.setView(view1);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                EditText type_of_data,user_name_field,user_credentials;
                Button submit;

                type_of_data = (EditText) view1.findViewById(R.id.data_type);
                user_name_field = (EditText) view1.findViewById(R.id.user_name_box);
                user_credentials = (EditText) view1.findViewById(R.id.user_credentials_field);
                submit = (Button) view1.findViewById(R.id.submit);


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(type_of_data.getText().toString().isEmpty()||user_name_field.getText().toString().isEmpty()||user_credentials.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Enter your data", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String data_type_enter = type_of_data.getText().toString();
                            String user_name_enter = user_name_field.getText().toString();
                            String credentials_enter = user_credentials.getText().toString();


                            try {
                                credentials_enter = AESUtils.encrypt(credentials_enter);
                                user_name_enter = AESUtils.encrypt(user_name_enter);
                                data_type_enter = AESUtils.encrypt(data_type_enter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            userDataList.add(new UserData(data_type_enter,user_name_enter,credentials_enter));
                            adapter.notifyItemInserted(userDataList.size());
                            alertDialog.dismiss();
                            saveData();

                            t1.setVisibility(View.GONE);
                            t2.setVisibility(View.GONE);

                        }



                    }
                });




            }
        });
    }



    private void recycler() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(recyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new Adapter(userDataList,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private void loadData() {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        Gson gson = new Gson();


        String json = sharedPreferences.getString("courses", null);


        Type type = new TypeToken<ArrayList<UserData>>() {}.getType();


        userDataList = gson.fromJson(json, type);

        if (userDataList == null) {
            userDataList = new ArrayList<>();
        }
    }


    private void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();


        String json = gson.toJson(userDataList);


        editor.putString("courses", json);


        editor.apply();

        Toast.makeText(this, "Data added successfully. ", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onRestart() {
        startActivity(new Intent(Vault.this,FingerprintAuth.class));
        finish();
        super.onRestart();
    }




    @Override
    protected void onStart() {
        if(adapter.getItemCount() != 0){
            t1.setVisibility(View.GONE);
            t2.setVisibility(View.GONE);
        }
        else{
            t1.setVisibility(View.VISIBLE);
            t2.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }
}