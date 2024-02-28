package com.example.passwordmanager;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class Adapter extends RecyclerView.Adapter<Adapter.Viewholder>{

    private ArrayList<UserData> userDataList;
    private Context context;
    public Adapter(ArrayList<UserData> userDataList,Context context){

        this.userDataList = userDataList;
        this.context = context;
    }
    @NonNull
    @Override
    public Adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_backgrounnd,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.Viewholder holder, int position) {
        String label = userDataList.get(position).getType();
        String user = userDataList.get(position).getUser_name();
        String credential = userDataList.get(position).getPassword();

        try {
            label = AESUtils.decrypt(label);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.setData(label,user,credential);
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public class Viewholder extends  RecyclerView.ViewHolder {
        private TextView label_top;
        private String credential_m,user_name_m;
        private ImageView copy,edit,delect;

        private static final int REQUEST_CODE = 5103;
        private Executor executor;
        private BiometricPrompt biometricPrompt;
        private BiometricPrompt.PromptInfo promptInfo;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            label_top = (TextView) itemView.findViewById(R.id.label);

            copy = (ImageView) itemView.findViewById(R.id.copy);
            edit = (ImageView) itemView.findViewById(R.id.edit);
             delect = (ImageView) itemView.findViewById(R.id.delect);



            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    executor = ContextCompat.getMainExecutor(context);
                    biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                            executor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode,
                                                          @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            Toast.makeText(context.getApplicationContext(),
                                    "Authentication error: " + errString, Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Toast.makeText(context.getApplicationContext(),
                                    "Copied Credential", Toast.LENGTH_SHORT).show();

                            int position = getAdapterPosition();
                            ClipboardManager clipboard;
                            clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            String pass = userDataList.get(position).getPassword();
                            try {
                                pass = AESUtils.decrypt(pass);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ClipData clip = ClipData.newPlainText("label",pass);
                            clipboard.setPrimaryClip(clip);


                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(context.getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

                    promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric Authentication")
                            .setSubtitle("Copy using your biometric credential")
                            .setNegativeButtonText("Cancel")
                            .build();

                    biometricPrompt.authenticate(promptInfo);





                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    executor = ContextCompat.getMainExecutor(context);
                    biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                            executor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode,
                                                          @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            Toast.makeText(context.getApplicationContext(),
                                    "Authentication error: " + errString, Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);


                            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View view2 = inflater.inflate(R.layout.custom_dialog_edit,null);
                            alert.setView(view2);
                            final AlertDialog alertDialog = alert.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                            EditText type_of_data,user_name_field,user_credentials;
                            Button edit;

                            type_of_data = (EditText) view2.findViewById(R.id.data_type_e);
                            user_name_field = (EditText) view2.findViewById(R.id.user_name_box_e);
                            user_credentials = (EditText) view2.findViewById(R.id.user_credentials_field_e);
                            edit = (Button) view2.findViewById(R.id.edit_data);
                            String type = userDataList.get(getAdapterPosition()).getType();
                            String user = userDataList.get(getAdapterPosition()).getUser_name();
                            String pass = userDataList.get(getAdapterPosition()).getPassword();

                            try {
                                pass = AESUtils.decrypt(pass);
                                user = AESUtils.decrypt(user);
                                type = AESUtils.decrypt(type);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            type_of_data.setText(type);
                            user_name_field.setText(user);
                            user_credentials.setText(pass);

                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                     String type = type_of_data.getText().toString();
                                     String user = user_name_field.getText().toString();
                                     String pass = user_credentials.getText().toString();

                                    try {
                                        type = AESUtils.encrypt(type);
                                        user = AESUtils.encrypt(user);
                                        pass = AESUtils.encrypt(pass);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    userDataList.get(getAdapterPosition()).setType(type);
                                     userDataList.get(getAdapterPosition()).setUser_name(user);
                                     userDataList.get(getAdapterPosition()).setPassword(pass);

                                     notifyItemChanged(getAdapterPosition());
                                     saveData();
                                     alertDialog.dismiss();
                                     Toast.makeText(context.getApplicationContext(), "Data Change Successfully", Toast.LENGTH_SHORT).show();


                                }
                            });


                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(context.getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

                    promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric Authentication")
                            .setSubtitle("Edit using your biometric credential")
                            .setNegativeButtonText("Cancel")
                            .build();

                    biometricPrompt.authenticate(promptInfo);



                }
            });


            delect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    userDataList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    saveData();
                    Toast.makeText(context.getApplicationContext(), "Data Removed Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setData(String label, String user, String credential){





            credential_m = credential;
            label_top.setText(label);
            user_name_m = user;


        }
    }

    private void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();


        String json = gson.toJson(userDataList);


        editor.putString("courses", json);


        editor.apply();



    }




}
