package com.facedetection.sugihart.facedetection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facedetection.sugihart.facedetection.Contact.ContactList;
import com.facedetection.sugihart.facedetection.User.UserList;

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

public class MainActivity extends AppCompatActivity {
    LinearLayout live_button,btn_profile,panic_button,alarm_button,record_button,registrasi_contact,registrasi_user,log_out,security_button;
    String login;

    String base_url_ip;
    String TokenSession;
    TextView txtnama,txtstatus;
    ImageView img_profile;
    SwipeRefreshLayout swiperefresh;
    public static MainActivity ma;
    CardView card_user;
    String is_admin = "";
    TextView txtstatus_actived;
    void init(){

        ma = this;
        img_profile = (ImageView) findViewById(R.id.img_profile);
        txtstatus_actived = (TextView) findViewById(R.id.txtstatus_actived);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        /*SharedPreferences pref2 = getApplicationContext().getSharedPreferences("pref_list_live",0);
        pref2.edit().putString("scene_name1","Camera 1").commit();
        pref2.edit().putString("scene_link1","http://122844270777.ip-dynamic.com:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/jbP7Zj87oU/s.mp4").commit();

        pref2.edit().putString("scene_name2","Camera 2").commit();
        pref2.edit().putString("scene_link2","http://122844270777.ip-dynamic.com:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/GXMVvFfuGa/s.mp4").commit();

        pref2.edit().putInt("n_scene",2).commit();
        pref2.edit().commit();*/


        TokenSession = pref.getString("token", null);
        base_url_ip = pref.getString("base_url", null);

        txtnama = (TextView) findViewById(R.id.txtnama);
        txtstatus = (TextView) findViewById(R.id.txtstatus);
        btn_profile = (LinearLayout) findViewById(R.id.btn_profile);
        panic_button = (LinearLayout) findViewById(R.id.panic_button);
        alarm_button = (LinearLayout) findViewById(R.id.alarm_button);
        record_button = (LinearLayout) findViewById(R.id.record_button);
        live_button = (LinearLayout) findViewById(R.id.live_button);
        registrasi_contact = (LinearLayout) findViewById(R.id.registrasi_contact);
        registrasi_user = (LinearLayout) findViewById(R.id.registrasi_user);
        log_out = (LinearLayout) findViewById(R.id.log_out);
        card_user = (CardView) findViewById(R.id.card_user);

        security_button = (LinearLayout) findViewById(R.id.security_button);
        try{
            is_admin =pref.getString("is_admin", null);
            if(is_admin.equals("0")){
                card_user.setVisibility(View.GONE);
            }else{
                card_user.setVisibility(View.VISIBLE);
            }

        }catch (Exception ex){
            card_user.setVisibility(View.VISIBLE);
            //Toast.makeText(ma, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        card_user.setVisibility(View.GONE);
        /**/
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            init();

            /*verifyStoragePermissions(MainActivity.this);*/

            if(isOnline(getApplicationContext())){
                txtstatus_actived.setText("Status : Actived");
                txtstatus.setText("Admin");
                getProfile();
            }else{
                txtstatus.setText("");
                txtstatus_actived.setText("");
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Tidak Ada Koneksi :( ")
                        .setConfirmText("Oke")
                        .show();
            }
            validate_login();
            btn_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),Profile.class));
                }
            });
            registrasi_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),ContactList.class));
                }
            });
            panic_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),PanicButton3.class));
                }
            });
            registrasi_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(),UserList.class);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("form","User");
                    editor.commit();
                    startActivity(in);
                }
            });
            log_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
            record_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(),AddLogImage.class);
                    in.putExtra("From","Menu");
                    startActivity(in);
                }
            });
            live_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),LiveStreaming.class));

                }
            });
            security_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(),SecurityList.class);
                    startActivity(in);

                }
            });
        }catch (Exception ex){
            Toast.makeText(ma, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    void validate_login(){

        login = getIntent().getStringExtra("login").toString();

        if(login.equals("sukses")){
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Success Login :) ")
                    .setConfirmText("Oke")
                    .show();
        }
    }
    public void logout(){

        android.support.v7.app.AlertDialog.Builder altBx = new AlertDialog.Builder(this);
        altBx.setTitle("Logout");
        altBx.setMessage("Yakin ingin Logout?");
        altBx.setIcon(R.drawable.ic_home_grey_800_24dp);
        altBx.setPositiveButton("Iya", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {


                SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                String base_url = pref.getString("base_url",null).toString();
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.commit();

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("base_url",base_url);
                editor.commit();

                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();

            }
        });
        altBx.setNegativeButton("Tidak", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                //show any message
            }

        });


        AlertDialog dialog = altBx.create();
        dialog .show();

        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

    }
/*
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }


    }*/
    public void getProfile(){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        //Toast.makeText(this, TokenSession, Toast.LENGTH_SHORT).show();

        FormBody body = new FormBody.Builder()
                .add("test","test").build();
        Request req = new Request.Builder()
                    .url(base_url_ip + "api/getuser")
                .post(body)
                .addHeader("Authorization","Bearer " + TokenSession)
                .addHeader("Accept","application/json")
                .build();
        Log.d("TOKENN",TokenSession);
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
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(responsebody);
                            JSONObject jsonfinal = jsonobject.getJSONObject("data");

                            txtnama.setText(jsonfinal.getString("name").toString());
                            //create session
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("name_admin",jsonfinal.getString("name").toString());
                            editor.putString("address_admin",jsonfinal.getString("address").toString());
                            editor.putString("phone_admin",jsonfinal.getString("phone").toString());
                            editor.putString("image_admin",jsonfinal.getString("image").toString());
                            editor.putString("regid",jsonfinal.getString("regid").toString());
                            editor.commit();
                            Glide.with(MainActivity.this)
                                    .load(base_url_ip + "storage/user/" + jsonfinal.getString("image").toString())
                                    .transform(new CircleTransform(getApplicationContext()))
                                    .into(img_profile);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, e.getMessage() ,Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
}
