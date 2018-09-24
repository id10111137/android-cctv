package com.facedetection.sugihart.facedetection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    Button btnlogin;
    EditText etEmail,etPw;
    ProgressBar prog;

    void init_component(){


        btnlogin = (Button) findViewById(R.id.btn_login);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPw = (EditText) findViewById(R.id.etPassword);
        prog = (ProgressBar) findViewById(R.id.prog_loading);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        try{

            //verifyStoragePermissions(LoginActivity.this);
            init_component();
            vallidate_auth();
            prog.setVisibility(View.INVISIBLE);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(getApplicationContext())){
                    try{
                        login();
                    }catch (Exception ex){
                        Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{

                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Tidak Ada Koneksi :( ")
                            .setConfirmText("Oke")
                            .show();
                }
            }
        });

        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void login(){
        prog.setVisibility(View.VISIBLE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        String base_url_ip = pref.getString("base_url", null);
        OkHttpClient client = new OkHttpClient();
        client.cache();

        String token2 = FirebaseInstanceId.getInstance().getToken();
        Log.d("token:", token2);

        FormBody body = new FormBody.Builder()
                .add("email",etEmail.getText().toString())
                .add("password",etPw.getText().toString())
                .add("token2",token2)
                .build();
        Request req = new Request.Builder().post(body).url(base_url_ip + "api/login").build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(LoginActivity.this, "Tidak Ada Koneksi", Toast.LENGTH_SHORT).show();
                prog.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(!response.isSuccessful()){
                    new IOException("error");
                }
                final String responsebody = response.body().string();

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("JSON : ",responsebody);
                        try {
                            JSONObject jsonobject = new JSONObject(responsebody);
                            prog.setVisibility(View.INVISIBLE);
                            JSONObject jsonfinal = jsonobject.getJSONObject("success");

                            String email = jsonfinal.getString("email").toString();
                            String token = jsonfinal.getString("token").toString();
                            String is_admin = jsonfinal.getString("is_admin").toString();
                            //create session
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("email",email);
                            editor.putString("token",token);
                            editor.putString("token_pw",etPw.getText().toString());
                            editor.putString("is_admin",is_admin);
                            editor.commit();
                            Intent in = new Intent(getApplicationContext(),Main2Activity.class);
                            in.putExtra("login","sukses");
                            startActivity(in);
                            finish();
                            //prog.setVisibility(View.INVISIBLE);

                        } catch (Exception e) {
                            Log.d("error json :",e.getMessage());
                            prog.setVisibility(View.INVISIBLE);

                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("Username and Password Wrong :( ")
                                    .setConfirmText("Oke")
                                    .show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void vallidate_auth(){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        String authlogin = pref.getString("token",null);
        try{
            if(authlogin.isEmpty()){
            }else{

                Intent in = new Intent(getApplicationContext(),Main2Activity.class);
                in.putExtra("login","not");
                startActivity(in);
                finish();
            }
        }catch (Exception ex){/*
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();*/
        }
    }

    public static boolean isOnline(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        // if running on emulator return true always.
        return android.os.Build.MODEL.equals("google_sdk");
    }


}
