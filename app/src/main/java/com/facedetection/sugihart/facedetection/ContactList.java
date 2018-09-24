package com.facedetection.sugihart.facedetection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContactList extends AppCompatActivity {
    String[] name_contact;
    String[] phone_contact;
    Integer[] id_contact;

    String[] name_contact_temp;
    String[] phone_contact_temp;
    Integer[] id_contact_temp;
    int pos_delete = 0;

    String base_url_ip;
    String TokenSession;
    GridView grid;
    TextView txtnodata;
    ImageView btn_delete;
    EditText txtsearch;
    public static ContactList CL;
    ArrayAdapter<String> ContactAdapter;
    SwipeRefreshLayout swiperefresh;
    void init(){
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        TokenSession = pref.getString("token", null);
        base_url_ip = pref.getString("base_url", null);
        Toast.makeText(getApplicationContext(), base_url_ip, Toast.LENGTH_SHORT).show();
        grid = (GridView) findViewById(R.id.grid);
        txtnodata = (TextView) findViewById(R.id.txtnodata);
        CL = this;
        txtsearch = (EditText) findViewById(R.id.txtsearch);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getDataContact();
                    }
                }
        );
        txtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                search_list();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_list();
            }

            @Override
            public void afterTextChanged(Editable s) {
                search_list();
            }
        });
    }
    boolean list_click = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        getSupportActionBar().setTitle("Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black_small);
        init();
        swiperefresh.setRefreshing(true);
        if(isOnline(getApplicationContext())){
            getDataContact();
        }else{

            swiperefresh.setRefreshing(false);
            new SweetAlertDialog(ContactList.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Tidak Ada Koneksi :( ")
                    .setConfirmText("Oke")
                    .show();
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
    public void search_list(){
        ArrayList<String> temp = new ArrayList<String>();
        ArrayList<String> temp_telepon = new ArrayList<String>();
        ArrayList<Integer> tempint = new ArrayList<Integer>();
        int textLength = txtsearch.getText().length();
        temp.clear();
        temp_telepon.clear();
        tempint.clear();
        //name
        try{
            for(int i=0;i<name_contact.length;i++){
                if(textLength <= name_contact[i].length()){
                    if(txtsearch.getText().toString().equalsIgnoreCase((String)name_contact[i].subSequence(0,textLength))){
                        temp.add(name_contact[i]);
                        tempint.add(id_contact[i]);
                        temp_telepon.add(phone_contact[i]);

                    }
                }
            }
            name_contact_temp = new String[temp.size()];
            name_contact_temp = temp.toArray(name_contact_temp);

            phone_contact_temp = new String[temp_telepon.size()];
            phone_contact_temp = temp_telepon.toArray(phone_contact_temp);

            //id
            id_contact_temp = new Integer[tempint.size()];
            id_contact_temp = tempint.toArray(id_contact_temp);

            ContactAdapter = new ListContactAdapter(ContactList.this);
            grid.setAdapter(ContactAdapter);
            ((ArrayAdapter)grid.getAdapter()).notifyDataSetInvalidated();

        }catch (Exception ex){

        }

        //event_grid();
    }
    public void getDataContact(){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        //Toast.makeText(this, TokenSession, Toast.LENGTH_SHORT).show();

        FormBody body = new FormBody.Builder()
                .add("test","test").build();
        Request req = new Request.Builder()
                .url(base_url_ip + "api/partnerlist")
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
                ContactList.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            swiperefresh.setRefreshing(false);
                            JSONObject jsonobject = new JSONObject(responsebody);
                            JSONArray DataArray = jsonobject.getJSONArray("data");

                            name_contact = new String[DataArray.length()];
                            phone_contact = new String[DataArray.length()];
                            id_contact =new Integer[DataArray.length()];

                            name_contact_temp = new String[DataArray.length()];
                            phone_contact_temp = new String[DataArray.length()];
                            id_contact_temp =new Integer[DataArray.length()];
                            for(int i=0;i<DataArray.length();i++){
                                JSONObject jsonfinal = DataArray.getJSONObject(i);
                                name_contact[i] = jsonfinal.getString("name");
                                phone_contact[i] = jsonfinal.getString("phone");
                                id_contact[i] = jsonfinal.getInt("id");

                                name_contact_temp[i] = name_contact[i];
                                phone_contact_temp[i] =phone_contact[i];
                                id_contact_temp[i] = id_contact[i];
                            }
                            if (DataArray.length() > 0){
                                ContactAdapter = new ListContactAdapter(ContactList.this);
                                grid.setAdapter(ContactAdapter);
                                ((ArrayAdapter)grid.getAdapter()).notifyDataSetInvalidated();

                                txtnodata.setText("");
                                event_grid();
                            }else{
                                txtnodata.setText("No Data Available");
                                ContactAdapter = new ListContactAdapter(ContactList.this);
                                grid.setAdapter(ContactAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            swiperefresh.setRefreshing(false);
                            Log.d("ERRRor",e.getMessage());
                            Toast.makeText(ContactList.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });
    }
    public void event_grid(){
        try{
            grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    //deleteDataContact(id_contact[position]);
                    //btn_delete.setVisibility(View.VISIBLE);
                    list_click = true;
                    grid.setItemChecked(position, true);

                    new SweetAlertDialog(ContactList.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Apakah Anda Yakin?")
                            .setContentText("Jika iya data anda akan hilang selamanya")
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
                                    if(position == 0){
                                        id_contact_temp =new Integer[0];
                                        deleteDataContact(id_contact[position]);

                                    }else{
                                        deleteDataContact(id_contact_temp[position]);
                                    }
                                    sDialog.dismissWithAnimation();
                                    list_click = false;
                                }
                            })
                            .show();
                    return false;

                }
            });

            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{

                        if (!list_click){
                            Intent in2 = new Intent(getApplicationContext(),EditAddContact.class);
                            in2.putExtra("id_contact",id_contact_temp[position]);
                            in2.putExtra("Event","Detail");
                            startActivity(in2);

                        }
                    }catch (Exception ex){
                        Toast.makeText(ContactList.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    //finish();

                }
            });


        }catch (Exception ex){
            Toast.makeText(CL, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public class ListContactAdapter extends ArrayAdapter<String> {

        public ListContactAdapter(Context ctx) {
            super(ctx, R.layout.item_list_contact, name_contact_temp);
        }
        public View getView(int position , View cView, ViewGroup parent){
            View row = cView;
            if(row == null){
                LayoutInflater lay = getLayoutInflater();
                row = lay.inflate(R.layout.item_list_contact,parent,false);
            }
            //ImageView img_contact = (ImageView) row.findViewById(R.id.img_contact);
            TextView txtname = (TextView) row.findViewById(R.id.txtname);
            TextView txtphone = (TextView) row.findViewById(R.id.txtphone);

            txtname.setText(name_contact_temp[position]);
            txtphone.setText(phone_contact_temp[position]);

            return row;
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
    public void deleteDataContact(Integer id_contact_par){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        TokenSession = pref.getString("token", null);
        //Toast.makeText(this, TokenSession, Toast.LENGTH_SHORT).show();

        FormBody body = new FormBody.Builder()
                .add("id", String.valueOf(id_contact_par)).build();
        Request req = new Request.Builder()
                .url(base_url_ip + "api/partner_delete")
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
                ContactList.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("JSON RESULT : ",responsebody);
                        try {
                            JSONObject jsonobject = new JSONObject(responsebody);
                            String result = jsonobject.getString("success").toString();

                            new SweetAlertDialog(ContactList.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success Delete Data :)")
                                    .setConfirmText("Oke")
                                    .show();
                            getDataContact();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERRRor",e.getMessage());
                            Toast.makeText(ContactList.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });
    }
}
