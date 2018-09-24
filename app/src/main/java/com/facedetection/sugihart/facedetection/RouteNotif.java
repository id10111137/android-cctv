package com.facedetection.sugihart.facedetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RouteNotif extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_notif);
        Intent in = new Intent(getApplicationContext(),AddLogImage.class);
        in.putExtra("From","Menu");
        startActivity(in);
    }
}
