package com.facedetection.sugihart.facedetection.Contact;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facedetection.sugihart.facedetection.AddLogImage;
import com.facedetection.sugihart.facedetection.CircleTransform;
import com.facedetection.sugihart.facedetection.ExceptionHandler;
import com.facedetection.sugihart.facedetection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

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

public class EditAddContact extends AppCompatActivity {
    ImageView img_profile,add_log,add_galery;


    FloatingActionButton add_button;
    TextView txttitle;
    EditText etName, etOccupation, etPhone, etAddress;
    Button btn_add_edit;
    int id_contact;

    public static EditAddContact EAC;
    public int pos = 0;
    int pos_edit = 0;
    String TypeUpload;
    String TokenSession;
    String base_url_ip;

    boolean list_click = false;

    //grid_log

    GridView grid,grid_galery;

    String id_log[]= new String[100];
    String date_log[] = new String[100];
    String time_log[] = new String[100];
    String img_log[] = new String[100];

    String temp_array_log[];
    String temp_array_galery[];

    boolean[] FromLog = new boolean[100];
    /*boolean[] LogImage = new boolean[100];*/
    void init() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        base_url_ip = pref.getString("base_url", null);
        txttitle = (TextView) findViewById(R.id.txttitle);
        etName = (EditText) findViewById(R.id.etName);
        etOccupation = (EditText) findViewById(R.id.etOccupation);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etAddress = (EditText) findViewById(R.id.etAddress);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        btn_add_edit = (Button) findViewById(R.id.btn_add_edit);

    }
    public boolean validate_input(){
        Boolean response = false;

        String name_ = etName.getText().toString();
        String phone_ = etPhone.getText().toString();
        String address_ = etAddress.getText().toString();
        String occup_ = etOccupation.getText().toString();
        if(name_.isEmpty() || phone_.isEmpty() || address_.isEmpty() || occup_.isEmpty()){
            response = false;
        }else{
            response = true;
        }
        return response;
    }
    void disable_input(){
        etName.setEnabled(false);
        etOccupation.setEnabled(false);
        etPhone.setEnabled(false);
        etAddress.setEnabled(false);
        btn_add_edit.setVisibility(View.INVISIBLE);
    }
    String type_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        EAC = this;
        type_event = getIntent().getStringExtra("Event").toString();
        try{

            if(type_event.equals("Detail")){
                setContentView(R.layout.activity_contact_detail);
                grid = (GridView) findViewById(R.id.grid);
            }else{
                setContentView(R.layout.activity_edit_add_contact2);
                grid = (GridView) findViewById(R.id.grid);

                grid_galery = (GridView) findViewById(R.id.grid_phone);

                add_galery = (ImageView) findViewById(R.id.add_galery);
            }


            add_log = (ImageView) findViewById(R.id.add_log);

            add_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] dialogitem = {"Kamera","Galery","Log"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditAddContact.this);
                    builder.setTitle("Menu");
                    builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case 0:
                                    TakeCamera();
                                    break;
                                case 1:
                                    Uploadimage();
                                    break;
                                case 2:

                                    Intent in = new Intent(getApplicationContext(),AddLogImage.class);
                                    in.putExtra("From","Contact");
                                    in.putExtra("id_log",id_log);
                                    in.putExtra("date_log",date_log);
                                    in.putExtra("time_log",time_log);
                                    in.putExtra("img_log",img_log);
                                    in.putExtra("FromLog_",FromLog );
                                    in.putExtra("nLog",nLog);
                                    startActivity(in);
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                 }
            });

            getSupportActionBar().setTitle(type_event);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black_small);

            init();

            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection", 0);
            TokenSession = pref.getString("token", null);

            txttitle.setText(type_event + " Data Contact");



            //upload_image_camera();

            btn_add_edit.setText(type_event);
            if(type_event.equals("Detail")){
                disable_input();

                add_button = (FloatingActionButton) findViewById(R.id.add_button);

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in2 = new Intent(getApplicationContext(),EditAddContact.class);
                        in2.putExtra("id_contact",id_contact);
                        in2.putExtra("Event","Update");
                        startActivity(in2);
                        finish();

                    }
                });
            }
            if(type_event.equals("Update") || type_event.equals("Detail")){
                id_contact = getIntent().getIntExtra("id_contact",0);
                pos = 0;
                if(isOnline(getApplicationContext())){
                    getDataContactById();
                }else{
                    new SweetAlertDialog(EditAddContact.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Tidak Ada Koneksi :( ")
                            .setConfirmText("Oke")
                            .show();
                }
            }else if(type_event.equals("Add")){
                try{

                    String[] id_ = getIntent().getStringArrayExtra("id_");
                    String[] date_ =getIntent().getStringArrayExtra("date_");
                    String[] time_ = getIntent().getStringArrayExtra("time_");
                    String[] img_ =getIntent().getStringArrayExtra("img_");
                    boolean[] FromLog_ =getIntent().getBooleanArrayExtra("FromLog_");
                    /*boolean[] LogImage_ =getIntent().getBooleanArrayExtra("LogImage_");*/
                    pos = getIntent().getIntExtra("pos",0);
                    setAdapterLogFromCheck(id_,date_,time_,img_,FromLog_/*LogImage_*/);
                }catch (Exception ex){
                    Toast.makeText(EAC, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            btn_add_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline(getApplicationContext())){
                        if (validate_input()){
                            AddUpdateData(type_event);
                        }else{

                            new SweetAlertDialog(EditAddContact.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Data Belum Lengkap")
                                    .setConfirmText("Oke")
                                    .show();
                        }
                    }else{

                        new SweetAlertDialog(EditAddContact.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Tidak Ada Koneksi :( ")
                                .setConfirmText("Oke")
                                .show();
                    }

                }
            });

            if(type_event.equals("Update") || type_event.equals("Add")){
                add_galery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CharSequence[] dialogitem = {"Kamera","Galery","Log"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAddContact.this);
                        builder.setTitle("Menu");
                        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        TakeCamera();
                                        break;
                                    case 1:
                                        Uploadimage();
                                        break;
                                    case 2:

                                        Intent in = new Intent(getApplicationContext(),AddLogImage.class);
                                        in.putExtra("From","Contact");
                                        in.putExtra("id_log",id_log);
                                        in.putExtra("date_log",date_log);
                                        in.putExtra("time_log",time_log);
                                        in.putExtra("img_log",img_log);
                                        in.putExtra("FromLog_",FromLog );
                                        in.putExtra("nLog",nLog);
                                        startActivity(in);
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    }
                });
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public int nLog = 0;
    public void getDataContactById(){
        OkHttpClient client = new OkHttpClient();
        client.cache();

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(id_contact))
                .build();

        final Request req = new Request.Builder()
                .post(body)
                .url(base_url_ip + "api/partner_show")
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
                EditAddContact.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonfinal = new JSONObject(responsebody);
                            JSONObject jsonfinal2 = jsonfinal.getJSONObject("data");

                            etName.setText(jsonfinal2.getString("name"));
                            etOccupation.setText(jsonfinal2.getString("occupation"));
                            etPhone.setText(jsonfinal2.getString("phone"));
                            etAddress.setText(jsonfinal2.getString("address"));
                            if (type_event.equals("Update") || type_event.equals("Detail")){
                                JSONArray jsonArray = jsonfinal2.getJSONArray("image");
                                temp_array_log= new String[jsonArray.length()];
                                pos =jsonArray.length();
                                pos_edit = jsonArray.length();
                                nLog = jsonArray.length();
                                for (int i=0;i < jsonArray.length();i++){

                                    JSONObject jsonfinal3 = jsonArray.getJSONObject(i);
                                    id_log[i] = jsonfinal3.getString("id").toString();
                                    date_log[i] = jsonfinal3.getString("created_at").substring(0,10).toString();
                                    time_log[i] = jsonfinal3.getString("created_at").substring(11).toString();
                                    img_log[i] = jsonfinal3.getString("image").toString();
                                    FromLog[i] = false;
                                    /*LogImage[i] = false;*/
                                }

                                if(jsonArray.length() > 0){
                                    /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) grid.getLayoutParams();
                                    int pos2 = 1;
                                    if (pos % 4 == 0){
                                        pos2 = pos/4;
                                    }
                                    else if(pos <= 3){
                                        pos2 = 1;
                                    }
                                    else{
                                        pos2 = (pos/4) + 1;
                                    }
                                    params.height = pos2 * 200;
                                    grid.setLayoutParams(params);*/


                                    grid.setAdapter(new LogAdapter(getApplicationContext()));
                                    grid_event();
                                    if (type_event.equals("Detail")){

                                        Glide.with(EditAddContact.this)
                                                .load(base_url_ip + "api/storage/"  +  img_log[0])
                                                .transform(new CircleTransform(getApplicationContext()))
                                                .into(img_profile);

                                    }


                                }else{
                                    txttitle.setText("- No Data Available -");
                                    grid.setVisibility(View.INVISIBLE);
                                    txttitle.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {

                            txttitle.setText(e.getMessage());

                            txttitle.setVisibility(View.VISIBLE);

                            e.printStackTrace();
                            //Toast.makeText(EditAddContact.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
    public void AddUpdateData(String Event) {
        try{
            final OkHttpClient client = new OkHttpClient();
            client.cache();

            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection", 0);
            TokenSession = pref.getString("token", null);
            MultipartBody body;
            Request req = null;
            btn_add_edit.setText("Menunggu..");

            if(Event.equals("Add")){

                MultipartBody.Builder form_builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                form_builder.addFormDataPart("occupation", etOccupation.getText().toString())
                        .addFormDataPart("name", etName.getText().toString())
                        .addFormDataPart("address", etAddress.getText().toString())
                        .addFormDataPart("phone", etPhone.getText().toString());
                String parseFoto = "";

                //foto galery
                for(int i=0;i<countGalery;i++) {
                    RequestBody file_body = RequestBody.create(MediaType.parse(mime[i]), fileimage[i]);
                    form_builder.addFormDataPart("galery[]","image_galery",file_body).build();
                }

                //log
                for(int i=0;i<pos;i++) {
                    form_builder.addFormDataPart("image[" +  String.valueOf(i) + "][image]",img_log[i]).build();
                    parseFoto +="image[" +  String.valueOf(i + countGalery) + "][image] = " + img_log[i] + " - ";
                }
                /* image[0][image] */

                Log.d("EAC",parseFoto);
                MultipartBody requestBody = form_builder.build();
                req = new Request.Builder()
                        .post(requestBody)
                        .url( base_url_ip + "api/partnerimageslog")
                        .addHeader("Authorization", "Bearer " + TokenSession)
                        .addHeader("Accept", "application/json")
                        .build();

            }else{
                MultipartBody.Builder form_builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                form_builder.addFormDataPart("id", String.valueOf(id_contact))
                        .addFormDataPart("occupation", etOccupation.getText().toString())
                        .addFormDataPart("name", etName.getText().toString())
                        .addFormDataPart("address", etAddress.getText().toString())
                        .addFormDataPart("phone", etPhone.getText().toString());
                String parseFoto = "";

                //foto galery
                for(int i=0;i<countGalery;i++) {
                    RequestBody file_body = RequestBody.create(MediaType.parse(mime[i]), fileimage[i]);
                    form_builder.addFormDataPart("galery[]","image_galery",file_body).build();
                }
                //log
                for(int i=0 + pos_edit;i<pos;i++) {
                    form_builder.addFormDataPart("image[" +  String.valueOf(i + countGalery) + "][image]",img_log[i]).build();
                    parseFoto +="image[" +  String.valueOf(i + countGalery) + "][image] = " + img_log[i] + " - ";
                }


                MultipartBody requestBody = form_builder.build();
                req = new Request.Builder()
                        .post(requestBody)
                        .url(base_url_ip + "api/partner_update")
                        .addHeader("Authorization", "Bearer " + TokenSession)
                        .addHeader("Accept", "application/json")
                        .build();


                Log.d("PARSE FOTO",parseFoto);
            }
            final Request finalReq = req;
            client.newCall(finalReq).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String responsebody = response.body().string();
                    Log.d("response",responsebody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    JSONObject jsonfinal = new JSONObject(responsebody);
                                    String res = jsonfinal.getString("success").toString();

                                    btn_add_edit.setText(type_event);
                                    new SweetAlertDialog(EditAddContact.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Success")
                                            .setContentText(type_event + " Data Success :)")
                                            .setConfirmText("Oke")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    //startActivity(new Intent(getApplicationContext(),ContactList.class));

                                                    ContactList.CL.getDataContact();

                                                    if (type_event.equals("Update")){
                                                        Intent in2 = new Intent(getApplicationContext(),EditAddContact.class);
                                                        in2.putExtra("id_contact",id_contact);
                                                        in2.putExtra("Event","Detail");
                                                        startActivity(in2);
                                                    }

                                                    finish();
                                                }
                                            })
                                            .show();



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(EditAddContact.this, "ERROR JSON" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(EditAddContact.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }

                        }
                    });
                }
            });


        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void resetArray(){
        int temp_i= 0;
        boolean getMove = false;
        for (int i = 0;i < pos;i++){
            if(id_log[i].equals("")){
                getMove = true;
                //Toast.makeText(getApplicationContext(), id_log[i], Toast.LENGTH_SHORT).show();
            }
            if (getMove){
                id_log[i] =  id_log[i + 1];
                date_log[i] =  date_log[i + 1];
                time_log[i] =  time_log[i + 1];
                img_log[i] =  img_log[i + 1];
            }
        }
    }
    public boolean validate_id(String id_){
        boolean response = false;
        for(int i =0;i < pos;i++){
            if(id_log[i].equals(id_)){
                response = true;
                return response;
            }
        }
        return response;
    }

    //Log
    public void setAdapterLogFromCheck(String[] id_ ,String[] date_ ,String[] time_,String[] img_,boolean[] FromLog_){
        try{
            /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) grid.getLayoutParams();
            int pos2 = 1;
            if (pos % 4 == 0){
                pos2 = pos/4;
            }
            else if(pos <= 3){
                pos2 = 1;
            }
            else{
                pos2 = (pos/4) + 1;
            }
            params.height = pos2 * 200;*/
            temp_array_log= new String[pos];
            id_log = id_;
            date_log = date_;
            time_log = time_;
            img_log = img_;
            FromLog = FromLog_;
            /*LogImage = LogImage_;*/
            grid.setAdapter(new LogAdapter(getApplicationContext()));
            grid_event();
        }catch (Exception ex){
            Toast.makeText(EAC, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void setAdapterLogImage(String id_ ,String date_ ,String time_,String img_){
        try{
            temp_array_log= new String[pos];

            /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) grid.getLayoutParams();
            int pos2 = 1;
            if (pos % 4 == 0){
                pos2 = pos/4;
            }
            else if(pos <= 3){
                pos2 = 1;
            }
            else{
                pos2 = (pos/4) + 1;
            }
            params.height = pos2 * 200;
            grid.setLayoutParams(params);*/

            FromLog[pos - 1] = true;
            /*LogImage[pos - 1] = true;*/

            id_log[pos - 1] = id_;
            date_log[pos - 1] = date_;
            time_log[pos - 1] = time_;
            img_log[pos - 1] = img_;
            grid.setAdapter(new LogAdapter(getApplicationContext()));
            grid_event();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("EAC",ex.getMessage());
        }

    }
    public void grid_event(){
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try{
                    if(!list_click){
                        LayoutInflater layoutInflater = (EditAddContact.this).getLayoutInflater();
                        View layout = layoutInflater.inflate(R.layout.detail_image, null);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditAddContact.this);
                        dialogBuilder.setView(layout);

                        ImageView img_detail = (ImageView) layout.findViewById(R.id.img_detail_image);
                        ImageView img_close = (ImageView) layout.findViewById(R.id.img_close);

                        String base_url = "";
                        if (type_event.equals("Update") || type_event.equals("Detail")){
                            base_url = base_url_ip +"api/storage/" /*+ String.valueOf(id_contact) + "/"*/;
                        }else{
                            base_url = base_url_ip +"api/storagelog/";
                        }
                        if(FromLog[position]){
                            base_url = base_url_ip +"api/storagelog/";
                        }else{
                            base_url = base_url_ip +"api/storage/" /*+ String.valueOf(id_contact) + "/"*/;
                        }
                        Glide.with(getApplicationContext())
                                .load(base_url + img_log[position])
                                //.resize(120, 60)
                                .error(R.drawable.noimage)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.progress_animation)
                                .into(img_detail);

                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();

                        img_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                list_click = false;
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                list_click = false;
                            }
                        });


                    }

                }catch (Exception ex){
                    Toast.makeText(EditAddContact.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                list_click = true;
                    new SweetAlertDialog(EditAddContact.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Apakah Anda Yakin?")
                            .setContentText("List data akan hilang")
                            .setConfirmText("Iya")
                            .setCancelText("Tidak")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    list_click = false;
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    id_log[position] = "";
                                    date_log[position] = "";
                                    time_log[position] = "";
                                    img_log[position] = "";
                                    pos = pos - 1;
                                    temp_array_log= new String[pos];
                                    resetArray();

                                    grid.setAdapter(new LogAdapter(getApplicationContext()));
                                    grid_event();
                                    list_click = false;
                                    sDialog.dismissWithAnimation();

                                }
                            })
                            .show();


                return false;
            }
        });

    }
    public class LogAdapter extends ArrayAdapter<String> {

        public LogAdapter(Context ctx) {
            super(ctx, R.layout.item_list_image, temp_array_log);
        }
        public View getView(int position , View cView, ViewGroup parent){

            View row = cView;
            if(row == null){
                LayoutInflater lay = getLayoutInflater();
                row = lay.inflate(R.layout.item_list_image,parent,false);
            }
        ImageView image_log = (ImageView) row.findViewById(R.id.img_log);
        TextView txttgl = (TextView) row.findViewById(R.id.txttgl);
        TextView txttime = (TextView) row.findViewById(R.id.txttime);


        String base_url = "";

            txttgl.setText(date_log[position]);
            txttime.setText(time_log[position]);

            if (type_event.equals("Update") || type_event.equals("Detail")){
                base_url =base_url_ip +"api/storage/" /*+ String.valueOf(id_contact) + "/"*/;
            }else{
                base_url = base_url_ip +"api/storagelog/";
            }

            try{
                if(FromLog[position]){
                    base_url = base_url_ip +"api/storagelog/";
                }
            }catch (Exception ex){
                //Toast.makeText(EditAddContact.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Glide.with(getApplicationContext())
                    .load(base_url + img_log[position])
                    //.resize(120, 60)
                    .error(R.drawable.noimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.progress_animation)
                    .into(image_log);
            //image_log.setImageResource(img_log[position]);




            return row;

        }
    }

    public class GaleryAdapter extends ArrayAdapter<String> {

        public GaleryAdapter(Context ctx) {
            super(ctx, R.layout.item_list_image, temp_array_galery);
        }
        public View getView(int position , View cView, ViewGroup parent){

            View row = cView;
            if(row == null){
                LayoutInflater lay = getLayoutInflater();
                row = lay.inflate(R.layout.item_list_image,parent,false);
            }
            ImageView image_log = (ImageView) row.findViewById(R.id.img_log);
            TextView txttgl = (TextView) row.findViewById(R.id.txttgl);
            TextView txttime = (TextView) row.findViewById(R.id.txttime);

            txttgl.setText("");
            txttime.setText("");

            String base_url = "";
            if (type_event.equals("Update") || type_event.equals("Detail")){
                base_url =base_url_ip +"api/storage/" /*+ String.valueOf(id_contact) + "/"*/;
            }else{
                base_url = base_url_ip +"api/storagelog/";
            }



            Glide.with(getApplicationContext())
                    .load(ImageUri[position])
                    //.resize(120, 60)
                    .error(R.drawable.noimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.progress_animation)
                    .into(image_log);
            //image_log.setImageResource(img_log[position]);



            return row;

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
    //CAMERA
    int PICK_IMAGE = 999;
    int PICK_CAMERA = 100;

    Uri file_camera;
    String[] mime = new String[100];

    int countGalery = 0;
    File[] fileimage = new File[100];
    Uri[] ImageUri = new Uri[100];;

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public void Uploadimage(){
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, PICK_IMAGE);
    }
    public void TakeCamera(){
        try{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                file_camera = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, file_camera);
                Log.d("FILEURI",file_camera+"");

                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(cameraIntent, PICK_CAMERA);
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("erro",ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try{
                ImageUri[countGalery] = data.getData();

                fileimage[countGalery] = new File(getRealPathFromURI(ImageUri[countGalery]));

                ContentResolver cR = getApplicationContext().getContentResolver();
                mime[countGalery] = cR.getType(ImageUri[countGalery]);

                countGalery+=1;

                temp_array_galery= new String[countGalery];
                grid_galery.setAdapter(new GaleryAdapter(getApplicationContext()));
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("eror image", ex.getMessage());
            }


        }else if(resultCode == RESULT_OK && requestCode == PICK_CAMERA){
            try{

                fileimage[countGalery] = new File(getRealPathFromURI(file_camera));
                ImageUri[countGalery] = file_camera;
                mime[countGalery] = "image/jpeg";
                countGalery +=1;

                temp_array_galery = new String[countGalery];

                grid_galery.setAdapter(new GaleryAdapter(getApplicationContext()));
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("test",ex.getMessage());
            }

        }

    }

}