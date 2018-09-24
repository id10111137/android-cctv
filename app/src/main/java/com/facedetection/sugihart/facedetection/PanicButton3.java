package com.facedetection.sugihart.facedetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PanicButton3 extends AppCompatActivity {
    ImageView btn_panic;
    String name,address,phone;

    String base_url_ip;
    String TokenSession;
    RippleBackground rippleBackground;
    TextView txt_panic,txt_wait;
    int panic_pos = 0;
    SharedPreferences pref;

    String[] phone_No;
    void init(){
        txt_panic = (TextView) findViewById(R.id.txt_panic);
        txt_wait = (TextView) findViewById(R.id.txt_wait);
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        TokenSession = pref.getString("token", null);
        base_url_ip = pref.getString("base_url", null);
        btn_panic = (ImageView) findViewById(R.id.btn_panic);
        name = pref.getString("name_admin", null);
        address = pref.getString("address_admin", null);
        phone = pref.getString("phone_admin", null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_button3);
        init();
        try{
            getSupportActionBar().setTitle("Panic Button");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black_small);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        getAllSecurity();
        try{
            if(checkPermission(android.Manifest.permission.SEND_SMS)) {

            }else {
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.SEND_SMS},
                        SEND_SMS_PERMISSION_REQUEST_CODE);
            }

        }catch (Exception ex){

        }

        final MediaPlayer[] mp = new MediaPlayer[1];
        btn_panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);

                    if(panic_pos == 2){
                        //send_panic();
                        mp[0] = MediaPlayer.create(getApplicationContext(), R.raw.location);
                        mp[0].start();
                        mp[0].setLooping(true);
                        txt_wait.setVisibility(View.VISIBLE);
                        txt_panic.setVisibility(View.INVISIBLE);

                        txt_wait.setText("Menunggu Response..");
                        panic_pos +=1;
                        /*send_panic();*/
                        send_sms_panic();
                        rippleBackground.startRippleAnimation();
                    }else if(panic_pos == 3){
                        mp[0].stop();
                        mp[0].release();
                        txt_wait.setVisibility(View.INVISIBLE);
                        txt_panic.setVisibility(View.VISIBLE);

                        panic_pos = 0;
                        rippleBackground.stopRippleAnimation();
                    }
                    else{
                        rippleBackground.stopRippleAnimation();
                        panic_pos +=1;
                        Toast.makeText(PanicButton3.this, "Press " + String.valueOf (3- panic_pos) +  " time the button again", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){

                }

            }
        });
    }
    public void send_sms_panic(){
        if(checkPermission(android.Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();

            String message,latitude,longitude;
            latitude = "-6.259697";
            longitude = "106.994801";
            String link_gmaps = "http://maps.google.com/maps?z=18&q=" + latitude + "," + longitude;
            message = "Tulung selamatkan rumah saya pak dari para janda muda, Ini Detail Tempat Rumah saya : \n" +
                      "Nama : " + name + " \n" +
                      "No HP : " + phone + " \n" +
                      "Alamat : " + address + " \n" +
                      "Link Gmaps : \n" + link_gmaps + " \n \n" +
                      "- Aegislite Security -";

            message = "INFO PENTING \nNama : " + name + "\nLink Gmaps : \n" + link_gmaps + " \nAda Orang tidak dikenal mohon dicek \n\n - Aegislite - ";


            for(int i=0;i < phone_No.length;i++){

                ArrayList<String> messageList = SmsManager.getDefault().divideMessage(message);
                if (messageList.size() > 1) {
                    smsManager.sendTextMessage(phone_No[i], null, String.valueOf(messageList), null, null);
                } else {
                    smsManager.sendTextMessage(phone_No[i], null, message, null, null);
                }
            }
            txt_wait.setText("Bantuan Berhasil Di kirim :)");
        }else {
            Toast.makeText(PanicButton3.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    public void getAllSecurity(){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        //Toast.makeText(this, TokenSession, Toast.LENGTH_SHORT).show();
        String url = "";
        url = base_url_ip + "api/security/get";

        FormBody body = new FormBody.Builder()
                .add("test","test").build();
        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization","Bearer " + TokenSession)
                .addHeader("Accept","application/json")
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
                PanicButton3.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonobject = new JSONObject(responsebody);
                            JSONArray DataArray = jsonobject.getJSONArray("data");

                            phone_No = new String[DataArray.length()];
                            for(int i=0;i<DataArray.length();i++){
                                JSONObject jsonfinal = DataArray.getJSONObject(i);
                                phone_No[i] = jsonfinal.getString("phone");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERRRor",e.getMessage());

                        }

                    }
                });
            }
        });
    }
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    private  Button mSendMessageBtn;

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mSendMessageBtn.setEnabled(true);
                }
                return;
            }
        }
    }
    public void send_panic(){

        OkHttpClient client = new OkHttpClient();
        client.cache();
        FormBody body = new FormBody.Builder()
                .add("name",name)
                .add("address",address)
                .add("phone",phone)
                .add("lat","-6.259697")
                .add("long","106.994801")
                .add("image",pref.getString("image_admin", null))
                .add("regid",pref.getString("regid", null))
                .build();
        Request req = new Request.Builder().post(body).url(getResources().getString(R.string.base_url_local)+ "index.php/api/send_panic").build();
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
                PanicButton3.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PanicButton3.this, "Berhasil", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(PanicButton3.this, responsebody, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
