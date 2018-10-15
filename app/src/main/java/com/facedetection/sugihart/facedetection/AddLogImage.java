package com.facedetection.sugihart.facedetection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView ;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facedetection.sugihart.facedetection.Contact.EditAddContact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddLogImage extends AppCompatActivity {
    GridView grid;
    String id_log[];
    String date_log[];
    String time_log[];
    String img_log[];
    boolean list_click = false;
    boolean list_detail = false;
    ImageView list_cancel,list_add;
    String base_url_ip;
    String TokenSession;
    SwipeRefreshLayout swiperefresh;
    TextView txtnodata;
    boolean item_check_visible = false;
    String getIntentFrom = "";
    boolean onback = true;

    List<LogAdapter> mYourCustomItems;
    void init(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        TokenSession = pref.getString("token", null);
        base_url_ip = pref.getString("base_url", null);

        grid = (GridView) findViewById(R.id.grid);
        list_cancel = (ImageView) findViewById(R.id.list_cancel);
        list_add = (ImageView) findViewById(R.id.list_add);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getDataLog();
                    }
                }
        );
        txtnodata = (TextView) findViewById(R.id.txtnodata);

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
        setContentView(R.layout.activity_add_log_image);
        getIntentFrom = getIntent().getStringExtra("From").toString();
        init();
        swiperefresh.setRefreshing(true);
        try{

            if(isOnline(getApplicationContext())){
                getDataLog();
            }else{
                new SweetAlertDialog(AddLogImage.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Tidak Ada Koneksi :( ")
                        .setConfirmText("Oke")
                        .show();
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("ALI",ex.getMessage());
        }


        list_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onback){
                    finish();
                }else{

                    try{
                        onback = true;
                        list_cancel.setImageResource(R.drawable.back_black);
                        item_check_visible = false;
                        pos_item_check.clear();
                        for(int i=0;i>positionArray.size();i++){
                            positionArray.set(i,false);
                        }
                        list_add.setVisibility(View.GONE);
                        grid.setAdapter(new LogAdapter(getApplicationContext()));
                        event_grid();
                    }catch (Exception ex){
                        Toast.makeText(AddLogImage.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        list_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    String[] id_ = new String[100];
                    String[]  date_ = new String[100];
                    String[] time_ = new String[100];
                    String[] img_ = new String[100];
                    boolean[] FromLog_ = new boolean[100];

                    String[] id_log_contact = new String[100];
                    String[]  date_log_contact = new String[100];
                    String[] time_log_contact = new String[100];
                    String[] img_log_contact = new String[100];
                    boolean[] FromLog_log_contact = new boolean[100];


                    if(getIntentFrom.equals("Contact")){

                        date_log_contact = getIntent().getStringArrayExtra("date_log");
                        time_log_contact = getIntent().getStringArrayExtra("time_log");
                        img_log_contact = getIntent().getStringArrayExtra("img_log");
                        FromLog_log_contact = getIntent().getBooleanArrayExtra("FromLog_");
                        id_log_contact = getIntent().getStringArrayExtra("id_log");
                        int nFromContact = getIntent().getIntExtra("nLog",0);

                        Log.d("Id Log Contact", Arrays.toString(id_log_contact));
                        Log.d("Date Log Contact", Arrays.toString(date_log_contact));
                        Log.d("Time Log Contact", Arrays.toString(time_log_contact));
                        Log.d("Img Log Contact", Arrays.toString(img_log_contact));

                        for (int i = 0;i < pos_item_check.size() + nFromContact;i++){
                            if(i >= nFromContact ){
                                int pos_list = pos_item_check.get(i - nFromContact);
                                FromLog_[i] = true;
                                id_[i] = id_log[pos_list];
                                date_[i] =date_log[pos_list];
                                time_[i] = time_log[pos_list];
                                img_[i] =img_log[pos_list];
                            }else{
                                FromLog_[i] = FromLog_log_contact[i];
                                id_[i] = id_log_contact[i];
                                date_[i] =date_log_contact[i];
                                time_[i] = time_log_contact[i];
                                img_[i] =img_log_contact[i];
                            }

                        }

                        /*Log.d("From Log", String.valueOf(FromLog_));*/

                        EditAddContact.EAC.nLog = pos_item_check.size() + nFromContact;
                        EditAddContact.EAC.pos = pos_item_check.size() + nFromContact;
                        EditAddContact.EAC.setAdapterLogFromCheck(id_,date_,time_,img_,FromLog_);
                        finish();
                    }else{


                        for (int i = 0;i<pos_item_check.size();i++){
                            int pos_list = pos_item_check.get(i);
                            FromLog_[i] = false;
                            id_[i] = id_log[pos_list];
                            date_[i] =date_log[pos_list];
                            time_[i] = time_log[pos_list];
                            img_[i] =img_log[pos_list];
                        }

                        Intent in = new Intent(getApplicationContext(),EditAddContact.class);
                        in.putExtra("Event","Add");
                        in.putExtra("pos",pos_item_check.size());
                        in.putExtra("id_",id_);
                        in.putExtra("date_",date_);
                        in.putExtra("time_",time_);
                        in.putExtra("img_",img_);
                        in.putExtra("FromLog_",FromLog_);
                        startActivity(in);
                        EditAddContact.EAC.setAdapterLogFromCheck(id_,date_,time_,img_,FromLog_);
                    }
                }catch (Exception ex){
                    Log.d("ALI",ex.getMessage());
                    Toast.makeText(AddLogImage.this, "ADD LOG " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void long_click_from_menu(){
        list_cancel.setVisibility(View.VISIBLE);
        list_add.setVisibility(View.VISIBLE);
        item_check_visible = true;

        ((ArrayAdapter)grid.getAdapter()).notifyDataSetChanged();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);

    }
    public void long_click_from_contact(int position){

        LayoutInflater layoutInflater = (AddLogImage.this).getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.detail_image, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddLogImage.this);
        dialogBuilder.setView(layout);

        ImageView img_detail = (ImageView) layout.findViewById(R.id.img_detail_image);
        ImageView img_close = (ImageView) layout.findViewById(R.id.img_close);

        Glide.with(getApplicationContext())
                .load(base_url_ip +"api/storagelog/" + img_log[position])
                //.resize(120, 60)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.noimage)
                .placeholder(R.drawable.progress_animation)
                .into(img_detail);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                list_click = false;
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_click = false;
                alertDialog.dismiss();
            }
        });

    }
    public void click_from_contact(int position){
        if (!EditAddContact.EAC.validate_id(id_log[position])) {
            EditAddContact.EAC.pos += 1;
            EditAddContact.EAC.setAdapterLogImage(id_log[position], date_log[position], time_log[position], img_log[position]);
            finish();
        } else {

            new SweetAlertDialog(AddLogImage.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Oops")
                    .setContentText("Data sudah ada di list")
                    .setConfirmText("Oke")
                    .show();

        }
    }
    ImageView image_log;
    TextView txttgl;
    TextView txttime;
    CheckBox item_check;
    int pos_add_array = 0;
    RadioGroup rGroup;
    ArrayList<Boolean> positionArray;
    ArrayList<Integer> pos_item_check = new ArrayList<Integer>();
    public class LogAdapter extends ArrayAdapter<String> {

        public LogAdapter(Context ctx) {
            super(ctx, R.layout.item_list_image, id_log);
            positionArray = new ArrayList<Boolean>(id_log.length);
            for(int i =0;i<id_log.length;i++){
                positionArray.add(false);
            }
        }
        public View getView(final int position , View cView, ViewGroup parent){

                View row = cView;
                if (row == null) {
                    LayoutInflater lay = getLayoutInflater();
                    row = lay.inflate(R.layout.item_list_image, parent, false);

                }

            image_log = (ImageView) row.findViewById(R.id.img_log);
            txttgl = (TextView) row.findViewById(R.id.txttgl);
            txttime = (TextView) row.findViewById(R.id.txttime);

            item_check = (CheckBox) row.findViewById(R.id.item_check);

            try {

                if (!item_check_visible) {
                    item_check.setVisibility(View.GONE);
                } else {
                    item_check.setVisibility(View.VISIBLE);
                }
                item_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            if (positionArray.get(position)) {
                                pos_add_array -=1;
                                positionArray.set(position, false);
                                pos_item_check.remove(pos_add_array);
                            } else {
                                pos_add_array +=1;
                                positionArray.set(position, true);
                                pos_item_check.add(position);
                            }

                            System.out.println(position+"--- :) : " + positionArray.get(position));
                        }catch (Exception ex){
                            //Toast.makeText(AddLogImage.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                item_check.setFocusable(false);
                item_check.setChecked(positionArray.get(position));
                //item_check.setChecked(false);x

                txttgl.setText(date_log[position]);
                txttime.setText(time_log[position]);
                Glide.with(getApplicationContext())
                        .load(base_url_ip +"api/storagelog/" + img_log[position])
                        //.resize(120, 60)
                        //.thumbnail(0.5f)
                        .error(R.drawable.noimage)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.progress_animation)
                        .into(image_log);
                //image_log.setImageResource(img_log[position]);
            }catch (Exception ex){
                Toast.makeText(AddLogImage.this, ex.getMessage()    , Toast.LENGTH_SHORT).show();
            }
            return row;
        }
    }
    /*public void uncheck_all_grid(){
        int count = grid.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            LinearLayout itemLayout = (LinearLayout)grid.getChildAt(i); // Find by under LinearLayout
            CheckBox CheckBox = (CheckBox)itemLayout.findViewById(R.id.item_check);
            CheckBox.setChecked(false);
        }
    }
    public void get_item_checked(){
        int count = grid.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            LinearLayout itemLayout = (LinearLayout)grid.getChildAt(i); // Find by under LinearLayout
            CheckBox CheckBox = (CheckBox)itemLayout.findViewById(R.id.item_check);
            if(CheckBox.isChecked())
            {
                Log.d("Item "+String.valueOf(i), CheckBox.getTag().toString());

                pos_item_check.add(i);
            }
        }
    }*/

    public void getDataLog(){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        //Toast.makeText(this, TokenSession, Toast.LENGTH_SHORT).show();

        FormBody body = new FormBody.Builder()
                .add("test","test").build();
        Request req = new Request.Builder()
                .url(base_url_ip + "api/get_log")
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
                AddLogImage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("JSON RESULT : ",responsebody);

                        try {
                            JSONObject jsonobject = new JSONObject(responsebody);
                            JSONArray DataArray = jsonobject.getJSONArray("data");
                            id_log = new String[DataArray.length()];
                            date_log = new String[DataArray.length()];
                            time_log=new String[DataArray.length()];
                            img_log =new String[DataArray.length()];
                            for(int i=0;i<DataArray.length();i++){
                                JSONObject jsonfinal = DataArray.getJSONObject(i);

                                id_log[i] = jsonfinal.getString("id");
                                /*date_log[i] = jsonfinal.getString("log").substring(0,10).toString();
                                time_log[i] = jsonfinal.getString("log").substring(11).toString();*/

                                date_log[i] = "";
                                time_log[i] = "";
                                //Toast.makeText(AddLogImage.this, jsonfinal.getString("image"), Toast.LENGTH_SHORT).show();


                                //img_log[i] = jsonfinal.getString("image");
                                img_log[i] = jsonfinal.getString("image");

                            }
                            if (DataArray.length() > 0) {
                                event_grid();
                            }else{
                                txtnodata.setVisibility(View.VISIBLE);
                            }

                            swiperefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERRRor",e.getMessage());
                            swiperefresh.setRefreshing(false);
                            Toast.makeText(AddLogImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });
    }
    public void event_grid(){
        grid.setAdapter(new LogAdapter(AddLogImage.this));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if(getIntentFrom.equals("Contact")){
                    list_detail = false;
                    if(!list_click ) {
                        click_from_contact(position);
                    }
                }

            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(getIntentFrom.equals("Contact")){
                    list_click = true;
                    /*long_click_from_contact(position);*/
                    long_click_from_menu();
                    list_cancel.setImageResource(R.drawable.cancel_black);
                }else{
                    long_click_from_menu();
                    onback = false;
                    list_cancel.setImageResource(R.drawable.cancel_black);
                }
                //list_click = false;

                return false;
            }
        });
    }

}
