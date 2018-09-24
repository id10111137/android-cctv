package com.facedetection.sugihart.facedetection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.*;

import android.Manifest;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.SparseArray;

import android.view.MenuItem;
import android.view.SurfaceHolder;

import android.view.SurfaceView;

import android.widget.Toast;



import com.google.android.gms.vision.CameraSource;

import com.google.android.gms.vision.Detector;

import com.google.android.gms.vision.barcode.Barcode;

import com.google.android.gms.vision.barcode.BarcodeDetector;



import java.io.IOException;




public class QRCode extends AppCompatActivity {
    SurfaceView cameraView;

    BarcodeDetector barcode;

    CameraSource cameraSource;

    SurfaceHolder holder;
    String form;
    boolean already_barcode = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode);
        try{
            getSupportActionBar().setTitle("QR CODE SCANNER");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black_small);


        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        form = getIntent().getStringExtra("form").toString();

        already_barcode = false;

        cameraView = (SurfaceView) findViewById(R.id.cameraView);

        cameraView.setZOrderMediaOverlay(true);

        holder = cameraView.getHolder();

        barcode = new BarcodeDetector.Builder(this)

                .setBarcodeFormats(Barcode.QR_CODE)

                .build();

        if (!barcode.isOperational()) {

            Toast.makeText(getApplicationContext(), "Sorry couldnt setup the detector", Toast.LENGTH_LONG).show();

            this.finish();

        }



        cameraSource = new CameraSource.Builder(this, barcode)

                .setFacing(CameraSource.CAMERA_FACING_BACK)

                .setRequestedFps(24)

                .setAutoFocusEnabled(true)

                .setRequestedPreviewSize(1920,1024)

                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override

            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {

                    if (ContextCompat.checkSelfPermission(QRCode.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        cameraSource.start(cameraView.getHolder());

                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }



            @Override

            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {



            }



            @Override

            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {



            }

        });

        barcode.setProcessor(new Detector.Processor<Barcode>() {

            @Override

            public void release() {



            }



            @Override

            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcode = detections.getDetectedItems();

                if (barcode.size() != 0) {
                    QRCode.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!already_barcode){
                                already_barcode = true;
                                if(form.equals("EditAdd")){
                                    LiveSceneAddEdit.LSAE.scene_link.setText(barcode.valueAt(0).displayValue);
                                    finish();
                                }else if(form.equals("SetIp")){
                                    SetIP.SIP.txtip.setText(barcode.valueAt(0).displayValue);
                                    finish();
                                }
                                else{
                                    Intent in = new Intent(getApplicationContext(),LiveSceneAddEdit.class);
                                    in.putExtra("form","Add");
                                    in.putExtra("scene_link",barcode.valueAt(0).displayValue);
                                    startActivity(in);
                                    finish();
                                }
                            }


                        }
                    });
                }

            }

        });

    }

}
