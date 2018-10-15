package com.facedetection.sugihart.facedetection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_handler);
        TextView txterror  = (TextView) findViewById(R.id.txterror);
        txterror.setText(getIntent().getStringExtra("error").toString());
    }
}
