package com.facedetection.sugihart.facedetection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SetIP extends AppCompatActivity {
    ImageView barcode;
    public EditText txtip;
    public static SetIP SIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);

        SIP = this;
        txtip = (EditText) findViewById(R.id.txtip);
        if(checkPermissions()){
            /*Toast.makeText(SI, "Permission Success", Toast.LENGTH_SHORT).show();*/
        }else{
            /*Toast.makeText(SI, "Permission Denied", Toast.LENGTH_SHORT).show();*/
        }
        final EditText etIP = (EditText) findViewById(R.id.txtip);
        Button btnsave = (Button) findViewById(R.id.btn_save);
        barcode = (ImageView) findViewById(R.id.scan_barcode);

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(),QRCode.class);
                in.putExtra("form","SetIp");
                startActivity(in);
            }
        });
        try{

            vallidate_ip();
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(SetIP.this, etIP.getText().toString(), Toast.LENGTH_SHORT).show();
                editor.putString("base_url",etIP.getText().toString());
                editor.commit();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
    }

    public boolean checkPermissions(){
        String[] permissions = new String[]{
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.VIBRATE,
                android.Manifest.permission.SEND_SMS
        };
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    public static void verifyCameraPermission(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA},100);
        }
    }
    void vallidate_ip(){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        String authlogin = pref.getString("base_url",null);
        try{
            if(authlogin.isEmpty()){
            }else{

                Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(in);
                finish();
            }
        }catch (Exception ex){/*
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();*/
        }
    }
}
