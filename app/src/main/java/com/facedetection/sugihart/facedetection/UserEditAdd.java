package com.facedetection.sugihart.facedetection;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserEditAdd extends AppCompatActivity {
    ImageView img_profile;


    FloatingActionButton add_button;
    TextView txttitle;
    EditText etName, etEmail, etPassword,etAddress,etPhone;
    Button btn_add_edit;
    int id_user;

    public static UserEditAdd EAC;
    public int pos = 0;
    String TokenSession;
    String base_url_ip,token_pw;
    String form;
    boolean list_click = false;

    //grid_log
    

    void init() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        base_url_ip = pref.getString("base_url", null);
        form = pref.getString("form", null);
        token_pw = pref.getString("token_pw",null);
        txttitle = (TextView) findViewById(R.id.txttitle);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhone = (EditText) findViewById(R.id.etPhone);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        btn_add_edit = (Button) findViewById(R.id.btn_add_edit);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = String.valueOf(s);
                try{

                    if(txt.subSequence(0,1).equals("*")){

                    }else{
                        pw_change = true;

                    }
                }catch (Exception ex){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    public boolean validate_input(){
        Boolean response = false;

        String name_ = etName.getText().toString();
        String Password_ = etPassword.getText().toString();
        String Email_ = etEmail.getText().toString();
        String phone_ = etPhone.getText().toString();
        String address_ = etAddress.getText().toString();
        if(name_.isEmpty() || Email_.isEmpty() ||  Password_.isEmpty() || phone_.isEmpty() || address_.isEmpty()){
            response = false;
        }else{
            response = true;
        }
        return response;
    }
    void disable_input(){
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        etPhone.setEnabled(false);
        etAddress.setEnabled(false);
        btn_add_edit.setVisibility(View.INVISIBLE);
    }
    String type_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EAC = this;
        type_event = getIntent().getStringExtra("Event").toString();
        try{

            if(type_event.equals("Detail")){
                setContentView(R.layout.activity_user_detail);
            }else{
                setContentView(R.layout.activity_user_add_edit);
            }
            getSupportActionBar().setTitle(type_event);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black_small);


            init();

            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection", 0);
            TokenSession = pref.getString("token", null);

            txttitle.setText(type_event + " Data " + form);

            //upload_image_camera();

            btn_add_edit.setText(type_event);
            if(type_event.equals("Detail")){
                disable_input();

                add_button = (FloatingActionButton) findViewById(R.id.add_button);

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in2 = new Intent(getApplicationContext(),UserEditAdd.class);
                        in2.putExtra("id_user",id_user);
                        in2.putExtra("Event","Update");
                        startActivity(in2);
                        finish();

                    }
                });
            }
            if(type_event.equals("Update") || type_event.equals("Detail")){
                id_user = getIntent().getIntExtra("id_user",0);

                if(isOnline(getApplicationContext())){
                    getDataUserById();
                }else{
                    new SweetAlertDialog(UserEditAdd.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Tidak Ada Koneksi :( ")
                            .setConfirmText("Oke")
                            .show();
                }
            }

            btn_add_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline(getApplicationContext())){
                        if (validate_input()){
                            AddUpdateData(type_event);
                        }else{

                            new SweetAlertDialog(UserEditAdd.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Data Belum Lengkap")
                                    .setConfirmText("Oke")
                                    .show();
                        }
                    }else{

                        new SweetAlertDialog(UserEditAdd.this, SweetAlertDialog.ERROR_TYPE)
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

    public void getDataUserById(){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        String url = "";
        url = base_url_ip + "api/getuserbyid";

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(id_user))
                .build();

        final Request req = new Request.Builder()
                .post(body)
                .url(url)
                .addHeader("Authorization", "Bearer " + TokenSession)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    new IOException("error");
                }
                final String responsebody = response.body().string();
                UserEditAdd.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonfinal = new JSONObject(responsebody);
                            JSONObject jsonfinal2 = jsonfinal.getJSONObject("data");

                            etName.setText(jsonfinal2.getString("name"));
                            etEmail.setText(jsonfinal2.getString("email"));
                            etPassword.setText("***********");
                            etPhone.setText(jsonfinal2.getString("phone"));
                            etAddress.setText(jsonfinal2.getString("address"));

                        } catch (JSONException e) {

                            txttitle.setText("- No Data Available -");

                            txttitle.setVisibility(View.VISIBLE);

                            e.printStackTrace();
                            //Toast.makeText(UserEditAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
    boolean pw_change = false;
    String token_pw2;
    public void AddUpdateData(String Event) {
        OkHttpClient client = new OkHttpClient();
        client.cache();
        try{


        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection", 0);
        TokenSession = pref.getString("token", null);
        FormBody body;
        Request req = null;

            Toast.makeText(EAC, String.valueOf(pw_change), Toast.LENGTH_SHORT).show();

        if(Event.equals("Add")){

            FormBody.Builder form_builder = new FormBody.Builder();
            String url = "";
            if(form.equals("User")){
                form_builder
                        .add("name", etName.getText().toString())
                        .add("password", etPassword.getText().toString())
                        .add("email", etEmail.getText().toString())
                        .add("username", etEmail.getText().toString())
                        .add("address",etAddress.getText().toString())
                        .add("phone",etPhone.getText().toString())
                        .add("is_admin","0");
                url = base_url_ip + "api/createuser";
            }

            body = form_builder.build();
            req = new Request.Builder()
                    .post(body)
                    .url( url)
                    .addHeader("Authorization", "Bearer " + TokenSession)
                    .addHeader("Accept", "application/json")
                    .build();

        }else{

            String url = "";

            url = base_url_ip + "api/updateuser";

            FormBody.Builder form_builder = new FormBody.Builder();

            if(pw_change){

                form_builder
                        .add("id", String.valueOf(id_user))
                        .add("name", etName.getText().toString())
                        .add("password", etPassword.getText().toString())
                        .add("email", etEmail.getText().toString())
                        .add("username", etEmail.getText().toString())
                        .add("address",etAddress.getText().toString())
                        .add("phone",etPhone.getText().toString())
                        .add("is_admin","0");
            }else{

                form_builder
                        .add("id", String.valueOf(id_user))
                        .add("name", etName.getText().toString())
                        .add("email", etEmail.getText().toString())
                        .add("username", etEmail.getText().toString())
                        .add("address",etAddress.getText().toString())
                        .add("phone",etPhone.getText().toString())
                        .add("is_admin","0");
                Toast.makeText(EAC, "False Kampret", Toast.LENGTH_SHORT).show();
            }

            body = form_builder.build();
            req = new Request.Builder()
                    .post(body)
                    .url(url)
                    .addHeader("Authorization", "Bearer " + TokenSession)
                    .addHeader("Accept", "application/json")
                    .build();

        }

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    new IOException("error");
                }
                final String responsebody = response.body().string();
                UserEditAdd.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("USER",responsebody);
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token_pw",token_pw2);
                        editor.commit();
                        try {

                            JSONObject jsonfinal = new JSONObject(responsebody);
                            String res = jsonfinal.getString("success").toString();

                            new SweetAlertDialog(UserEditAdd.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setContentText(type_event + " Data Success :)")
                                    .setConfirmText("Oke")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            //startActivity(new Intent(getApplicationContext(),UserList.class));
                                            UserList.CL.getDataUser();
                                            if (type_event.equals("Update")){
                                                Intent in2 = new Intent(getApplicationContext(),UserEditAdd.class);
                                                in2.putExtra("id_user",id_user);
                                                in2.putExtra("Event","Detail");
                                                startActivity(in2);
                                            }
                                            finish();
                                        }
                                    })
                                    .show();



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(UserEditAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        }catch (Exception ex){
            Toast.makeText(EAC, ex.getMessage(), Toast.LENGTH_SHORT).show();
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}