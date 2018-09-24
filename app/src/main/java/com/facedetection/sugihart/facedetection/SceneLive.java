package com.facedetection.sugihart.facedetection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SceneLive extends AppCompatActivity {
    /*Button btnscene1,btnscen2;*/
    ImageView list_add;
    GridView grid;
    public static SceneLive SL;
    SharedPreferences pref;

    String[] scene_name;
    String[] scene_link;
    boolean list_click = false;
    ImageView scan_barcode;

    void init(){
        SL = this;
        list_add = (ImageView) findViewById(R.id.list_add);
        grid = (GridView) findViewById(R.id.grid);

    }
    //method scanBarcode
    private void scanBarcode(String mode) {
        try {
            //buat intent untuk memanggil fungsi scan pada aplikasi zxing
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", mode); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 1);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("com.google.zxing.client.android.SCAN");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Tanggkap hasil dari scan
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                String contents = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(getBaseContext(), "Hasil :"+contents, Toast.LENGTH_SHORT).show();

            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_live);
        try{

            init();
            getDataLive();
            list_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String[] colors = {"Manual", "QR CODE"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(SceneLive.this);
                    builder.setTitle("Option");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){

                                Intent in = new Intent(getApplicationContext(),LiveSceneAddEdit.class);
                                in.putExtra("form","Add");
                                in.putExtra("scene_link","");
                                startActivity(in);
                            }else if(which == 1){

                                Intent in = new Intent(getApplicationContext(),QRCode.class);
                                in.putExtra("form","List");
                                startActivity(in);
                            }
                        }
                    });

                    builder.show();

                }
            });
            /*btnscene1 = (Button) findViewById(R.id.btnscene1);
            btnscen2 = (Button) findViewById(R.id.btnscene2);
            btnscene1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in= new Intent(getApplicationContext(),LiveStreaming.class);
                    in.putExtra("url","http://122844270777.ip-dynamic.com:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/jbP7Zj87oU/s.mp4");
                    startActivity(in);

                }
            });
            btnscen2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in= new Intent(getApplicationContext(),LiveStreaming.class);
                        in.putExtra("url","http://122844270777.ip-dynamic.com:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/GXMVvFfuGa/s.mp4");
                    startActivity(in);

                }
            });*/
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void getDataLive(){

        pref = getApplicationContext().getSharedPreferences("pref_list_live",0);
        int n_scene = pref.getAll().size() - 1 ;
        n_scene = n_scene / 2;

            scene_name = new String[n_scene];
            scene_link = new String[n_scene];
            int i = 0;
            int j = 0;

            Map<String,?> entries = pref.getAll();
            Set<String> keys = entries.keySet();
            String[] keyss = new String[keys.size()];
            int k = 0;
            for (String key : keys) {
                keyss[k] = key;
                k++;
            }
            for(int m=keyss.length - 1;m >= 0;m--){
                try{
                    String key = keyss[m];
                    if(key.substring(0,10).equals("scene_name")){
                        scene_name[i] = pref.getString(key,null);
                        i+=1;
                    }
                    if(key.substring(0,10).equals("scene_link")){
                        scene_link[j] = pref.getString(key,null);
                        j+=1;
                    }

                }catch (Exception ex){

                }
            }

            grid.setAdapter(new SceneLiveAdapter(getApplicationContext()));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (!list_click) {
                        Intent in = new Intent(getApplicationContext(), LiveStreaming.class);
                        in.putExtra("url", scene_link[position]);
                        startActivity(in);
                    }
                }
            });
            grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    list_click =true;
                    try{
                        String[] colors = {"Edit", "Delete"};

                        grid.setItemChecked(position, true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SceneLive.this);
                        builder.setTitle("Option");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){

                                    list_click = false;
                                    Intent in = new Intent(getApplicationContext(),LiveSceneAddEdit.class);
                                    in.putExtra("form","Update");
                                    in.putExtra("id_scene",position + 1);
                                    startActivity(in);
                                }else if(which == 1){

                                    new SweetAlertDialog(SceneLive.this, SweetAlertDialog.WARNING_TYPE)
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
                                                    int pos = position + 1;
                                                    pref.edit().remove("scene_name" + pos).commit();
                                                    pref.edit().remove("scene_link" + pos).commit();
                                                    pref.edit().remove("posN" + pos).commit();
                                                    int n_scene2 = pref.getInt("n_scene",0) - 1;
                                                    pref.edit().putInt("n_scene",n_scene2).commit();
                                                    list_click = false;
                                                    sDialog.dismissWithAnimation();
                                                    getDataLive();
                                                    new SweetAlertDialog(SceneLive.this, SweetAlertDialog.SUCCESS_TYPE)
                                                            .setTitleText("Sukses")
                                                            .setContentText("Berhasil Menghapus Data!!")
                                                            .setConfirmText("Oke")
                                                            .show();
                                                }
                                            })
                                            .show();
                                }
                            }
                        });

                        builder.show();
                    }catch (Exception ex){

                    }

                    return false;
                }
            });

        //SharedPreferences.Editor editor = pref.edit();
    }
    public class SceneLiveAdapter extends ArrayAdapter<String> {

        public SceneLiveAdapter(Context ctx) {
            super(ctx, R.layout.item_live_camera, scene_name);
        }
        public View getView(int position , View cView, ViewGroup parent){
            View row = cView;
            if(row == null){
                LayoutInflater lay = getLayoutInflater();
                row = lay.inflate(R.layout.item_live_camera,parent,false);
            }
            //ImageView img_user = (ImageView) row.findViewById(R.id.img_user);
            TextView txtname = (TextView) row.findViewById(R.id.txtname);

            txtname.setText(scene_name[position]);

            return row;
        }
    }
}
