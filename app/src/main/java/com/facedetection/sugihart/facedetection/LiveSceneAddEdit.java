package com.facedetection.sugihart.facedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LiveSceneAddEdit extends AppCompatActivity {
    ImageView scan_barcode;
    public static EditText scene_link,scene_name;
    Button btn_add_edit;
    String form;
    int n_scene = 0;
    SharedPreferences pref;
    public static LiveSceneAddEdit LSAE;

    void init(){
        LSAE = this;
        pref = getApplicationContext().getSharedPreferences("pref_list_live",0);

        form = getIntent().getStringExtra("form").toString();

        scene_link = (EditText) findViewById(R.id.scene_link);
        scene_name = (EditText) findViewById(R.id.scene_name);
        btn_add_edit = (Button) findViewById(R.id.btn_add_edit);
        n_scene = pref.getInt("n_scene",0);
        scan_barcode = (ImageView) findViewById(R.id.scan_barcode);
        scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),QRCode.class);
                in.putExtra("form","EditAdd");
                startActivity(in);
            }
        });

        try{

            if(form.equals("Update")){
                int id_scene = getIntent().getIntExtra("id_scene",0);

                scene_name.setText(pref.getString("scene_name" + String.valueOf(id_scene),null).toString());
                scene_link.setText(pref.getString("scene_link" + String.valueOf(id_scene),null).toString());
            }

            if(form.equals("Add")){
                scene_link.setText(getIntent().getStringExtra("scene_link").toString());
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_scene_add_edit);
        init();

        try{
            getSupportActionBar().setTitle(form + " Scene");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black_small);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        btn_add_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(form.equals("Add")) {
                   try{
                       final SharedPreferences.Editor editor = pref.edit();
                       n_scene +=1;
                       new SweetAlertDialog(LiveSceneAddEdit.this, SweetAlertDialog.SUCCESS_TYPE)
                               .setTitleText("Success")
                               .setContentText(form + " Data Success :)")
                               .setConfirmText("Oke")
                               .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                   @Override
                                   public void onClick(SweetAlertDialog sweetAlertDialog) {
                                       //startActivity(new Intent(getApplicationContext(),UserList.class));

                                       editor.putString("scene_name" + n_scene,scene_name.getText().toString());
                                       editor.putString("scene_link" + n_scene,scene_link.getText().toString());
                                       editor.putInt("n_scene",n_scene);
                                       editor.commit();
                                       SceneLive.SL.getDataLive();
                                       finish();
                                   }
                               }).show();
                   }catch (Exception ex){
                       Toast.makeText(LiveSceneAddEdit.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                   }

               }else{

                   try{
                       final SharedPreferences.Editor editor = pref.edit();
                       n_scene = getIntent().getIntExtra("id_scene",0);
                       new SweetAlertDialog(LiveSceneAddEdit.this, SweetAlertDialog.SUCCESS_TYPE)
                               .setTitleText("Success")
                               .setContentText(form + " Data Success :)")
                               .setConfirmText("Oke")
                               .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                   @Override
                                   public void onClick(SweetAlertDialog sweetAlertDialog) {
                                       //startActivity(new Intent(getApplicationContext(),UserList.class));

                                       editor.putString("scene_name" + n_scene,scene_name.getText().toString());
                                       editor.putString("scene_link" + n_scene,scene_link.getText().toString());
                                       editor.commit();
                                       SceneLive.SL.getDataLive();
                                       finish();
                                   }
                               }).show();
                   }catch (Exception ex){
                       Toast.makeText(LiveSceneAddEdit.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });
    }
}
