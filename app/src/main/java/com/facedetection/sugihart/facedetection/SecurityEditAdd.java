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

public class SecurityEditAdd extends AppCompatActivity {
    ImageView img_profile;


    FloatingActionButton add_button;
    TextView txttitle;
    EditText etName, etOccupation,etPhone;
    Button btn_add_edit;
    int id_security;

    public static SecurityEditAdd EAC;
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
        etOccupation = (EditText) findViewById(R.id.etOccupation);
        etPhone = (EditText) findViewById(R.id.etPhone);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        btn_add_edit = (Button) findViewById(R.id.btn_add_edit);

    }
    public boolean validate_input(){
        Boolean response = false;

        String name_ = etName.getText().toString();
        String phone_ = etPhone.getText().toString();
        String occupation_ = etOccupation.getText().toString();
        if(name_.isEmpty() || phone_.isEmpty() || occupation_.isEmpty()){
            response = false;
        }else{
            response = true;
        }
        return response;
    }
    void disable_input(){
        etName.setEnabled(false);
        etPhone.setEnabled(false);
        etOccupation.setEnabled(false);
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
                setContentView(R.layout.activity_security_detail);
            }else{
                setContentView(R.layout.activity_security_edit_add);
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
                        Intent in2 = new Intent(getApplicationContext(),SecurityEditAdd.class);
                        in2.putExtra("id_security",id_security);
                        in2.putExtra("Event","Update");
                        startActivity(in2);
                        finish();

                    }
                });
            }
            if(type_event.equals("Update") || type_event.equals("Detail")){
                id_security = getIntent().getIntExtra("id_security",0);

                if(isOnline(getApplicationContext())){
                    getDataUserById();
                }else{
                    new SweetAlertDialog(SecurityEditAdd.this, SweetAlertDialog.ERROR_TYPE)
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

                            new SweetAlertDialog(SecurityEditAdd.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Data Belum Lengkap")
                                    .setConfirmText("Oke")
                                    .show();
                        }
                    }else{

                        new SweetAlertDialog(SecurityEditAdd.this, SweetAlertDialog.ERROR_TYPE)
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
        url = base_url_ip + "api/security/show";

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(id_security))
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
                SecurityEditAdd.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonfinal = new JSONObject(responsebody);

                            JSONArray jsonfinal2 = jsonfinal.getJSONArray("data");
                            etName.setText(jsonfinal2.getJSONObject(0).getString("name"));
                            
                            etPhone.setText(jsonfinal2.getJSONObject(0).getString("phone"));
                            etOccupation.setText(jsonfinal2.getJSONObject(0).getString("occupation"));

                        } catch (JSONException e) {

                            txttitle.setText("- No Data Available -");
                            Toast.makeText(SecurityEditAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            txttitle.setVisibility(View.VISIBLE);

                            e.printStackTrace();
                            //Toast.makeText(SecurityEditAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
    public void AddUpdateData(String Event) {
        OkHttpClient client = new OkHttpClient();
        client.cache();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection", 0);
        TokenSession = pref.getString("token", null);
        FormBody body;
        Request req = null;



        if(Event.equals("Add")){

            FormBody.Builder form_builder = new FormBody.Builder();
            String url = "";
            form_builder
                        .add("name", etName.getText().toString())
                        .add("occupation",etOccupation.getText().toString())
                        .add("phone",etPhone.getText().toString())
                        .add("active","1");
            url = base_url_ip + "api/security/create";
            

            body = form_builder.build();
            req = new Request.Builder()
                    .post(body)
                    .url( url)
                    .addHeader("Authorization", "Bearer " + TokenSession)
                    .addHeader("Accept", "application/json")
                    .build();

        }else{

            String url = "";

            url = base_url_ip + "api/security/update";

            FormBody.Builder form_builder = new FormBody.Builder();

            
                form_builder
                        .add("id", String.valueOf(id_security))
                        .add("name", etName.getText().toString())
                        .add("occupation",etOccupation.getText().toString())
                        .add("phone",etPhone.getText().toString())
                        .add("active","1");
            
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
                SecurityEditAdd.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("SECURITY",responsebody);
                        try {

                            JSONObject jsonfinal = new JSONObject(responsebody);
                            String res = jsonfinal.getString("success").toString();

                            new SweetAlertDialog(SecurityEditAdd.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setContentText(type_event + " Data Success :)")
                                    .setConfirmText("Oke")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            //startActivity(new Intent(getApplicationContext(),SecurityList.class));
                                            SecurityList.CL.getDataSecurity();
                                            if (type_event.equals("Update")){
                                                Intent in2 = new Intent(getApplicationContext(),SecurityEditAdd.class);
                                                in2.putExtra("id_security",id_security);
                                                in2.putExtra("Event","Detail");
                                                startActivity(in2);
                                            }
                                            finish();
                                        }
                                    })
                                    .show();



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SecurityEditAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
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