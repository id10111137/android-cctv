package com.facedetection.sugihart.facedetection;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressWarnings("deprecation")
public class Main2Activity extends TabActivity {
    TabHost tabhost;
    String label_tab[] = {"Home","Live Stream","Profile"};
    int draw_tab[] = {R.drawable.i2_home,R.drawable.i2_live,R.drawable.i2_profile};
    int draw_tab_sel[] = {R.drawable.i2_home_orange,R.drawable.i2_live_orange,R.drawable.i2_profile_orange};
    String login;

    void validate_login(){

        login = getIntent().getStringExtra("login").toString();

        if(login.equals("sukses")){
            new SweetAlertDialog(Main2Activity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Success Login :) ")
                    .setConfirmText("Oke")
                    .show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        try{
            tabhost = getTabHost();
            TabHost.TabSpec spec;
            Intent intent;

            intent = new Intent().setClass(this, MainActivity.class);//content pada tab yang akan kita buat
            intent.putExtra("login","not");
            spec = tabhost.newTabSpec("0").setIndicator(createTabIndicator(label_tab[0],draw_tab[0])).setContent(intent);//mengeset nama tab dan mengisi content pada menu tab anda.
            tabhost.addTab(spec);//untuk membuat tabbaru disini bisa diatur sesuai keinginan anda

            intent = new Intent().setClass(this, SceneLive.class);
            spec = tabhost.newTabSpec("1").setIndicator(createTabIndicator(label_tab[1],draw_tab[1])).setContent(intent);
            tabhost.addTab(spec);

            intent = new Intent().setClass(this, Profile.class);
            spec = tabhost.newTabSpec("2").setIndicator(createTabIndicator(label_tab[2],draw_tab[2])).setContent(intent);
            tabhost.addTab(spec);


            tabhost.setCurrentTab(0);



            tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {

                    // TODO Auto-generated method stub
                    setDefaultTab();
                        View view = tabhost.getTabWidget().getChildTabViewAt(Integer.valueOf(tabId));
                        if ( view != null ) {
                            // get title text view
                            TextView textView = (TextView) view.findViewById(R.id.title);
                            textView.setTextColor(getResources().getColor(R.color.orange));

                            ImageView icon = (ImageView) view.findViewById(R.id.icon);
                            icon.setImageResource(draw_tab_sel[Integer.valueOf(tabId)]);
                        }


                }
            });


        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("MAIN2",ex.getMessage());
        }

    }
    public void setDefaultTab(){
        for(int i=0;i<=2;i++)
        {
            View tabIndicator = tabhost.getTabWidget().getChildTabViewAt(i);
            if ( tabIndicator != null ) {
                TextView textView = (TextView) tabIndicator.findViewById(R.id.title);
                textView.setTextColor(getResources().getColor(R.color.control));
                ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
                icon.setImageResource(draw_tab[i]);
            }

        }
    }
    private View createTabIndicator(String label,int draw) {
        View tabIndicator;
        if(label.equals("Home")){

            tabIndicator = getLayoutInflater().inflate(R.layout.item_tabhost, null);
            TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
            tv.setText(label);
            tv.setTextColor(getResources().getColor(R.color.orange));
            ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
            icon.setImageResource(draw_tab_sel[0]);
        }else{

            tabIndicator = getLayoutInflater().inflate(R.layout.item_tabhost, null);
            TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
            tv.setText(label);
            ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
            icon.setImageResource(draw);

        }
        return tabIndicator;
    }

}
