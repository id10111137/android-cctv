package com.facedetection.sugihart.facedetection;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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


public class Profile extends AppCompatActivity {
ImageView img_profile;
    int PICK_IMAGE = 999;
    int PICK_CAMERA = 100;

    String mime;
    File fileimage;
    String TypeUpload;
    Uri ImageUri;
    Uri file_camera;
    ImageView img_takecamera,pencil_fullname;
    Button btn_upload,btn_upload_server;
    EditText etEmail,etPassword,etPhone,etAddress,etFullname;
    ProgressDialog progress;
    String base_url_ip,token_pw;
    String TokenSession;
    public static Profile prof;

    void init(){
        prof = this;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
        TokenSession = pref.getString("token", null);
        base_url_ip = pref.getString("base_url", null);
        token_pw = pref.getString("token_pw",null);
        img_takecamera = (ImageView) findViewById(R.id.img_takecamera);
        etFullname = (EditText) findViewById(R.id.etFullname);
        etEmail=(EditText) findViewById(R.id.etEmail);
        etPassword=(EditText) findViewById(R.id.etPassword);
        etPhone=(EditText) findViewById(R.id.etPhone);
        etAddress=(EditText) findViewById(R.id.etAddress);
        btn_upload_server = (Button) findViewById(R.id.btn_upload_server);
        pencil_fullname = (ImageView) findViewById(R.id.pencil_fullname);

    }
    Boolean pw_disabled = false;
    void disable_input(){

        etEmail.setInputType(InputType.TYPE_NULL);
        etPassword.setInputType(InputType.TYPE_NULL);
        etPhone.setInputType(InputType.TYPE_NULL);
        etAddress.setInputType(InputType.TYPE_NULL);
        etFullname.setInputType(InputType.TYPE_NULL);
        btn_upload_server.setEnabled(false);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        /*try{
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        verifyCameraPermission(Profile.this);
        init();
        if(isOnline(getApplicationContext())){

            getProfile();
        }else{


            new SweetAlertDialog(Profile.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Tidak Ada Koneksi :( ")
                    .setConfirmText("Oke")
                    .show();
        }
        disable_input();
        EventDrawable();
        btn_upload_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(isOnline(getApplicationContext())){
                        btn_upload_server.setText("Menunggu..");
                        btn_upload_server.setEnabled(false);
                        /*progress = new ProgressDialog(Profile.this);
                        progress.setTitle("Loading");
                        progress.setMessage("Wait while loading...");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();*/

                        UploadToServer(fileimage);
                    }else{

                        new SweetAlertDialog(Profile.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Tidak Ada Koneksi :( ")
                                .setConfirmText("Oke")
                                .show();
                    }
                }catch (Exception ex){
                    Toast.makeText(Profile.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        img_profile = (ImageView) findViewById(R.id.img_profile);
        img_takecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] dialogitem = {"Ambil dari kamera", "Foto Galery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
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
                                }
                            }
                        });

                        builder.create().show();

            }
        });




    }
    String id_contact;
    public void getProfile(){
        OkHttpClient client = new OkHttpClient();
        client.cache();
        //Toast.makeText(this, TokenSession, Toast.LENGTH_SHORT).show();

        FormBody body = new FormBody.Builder()
                .add("test","test").build();
        Request req = new Request.Builder()
                .url(base_url_ip + "api/getuser")
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
                Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(responsebody);
                            JSONObject jsonfinal = jsonobject.getJSONObject("data");
                            id_contact = String.valueOf(jsonfinal.getString("id").toString());
                            etFullname.setText(jsonfinal.getString("name").toString());
                            etEmail.setText(jsonfinal.getString("email").toString());
                            etAddress.setText(jsonfinal.getString("address").toString());
                            etPhone.setText(jsonfinal.getString("phone").toString());
                            Glide.with(Profile.this)
                                    .load(base_url_ip + "storage/user/" + jsonfinal.getString("image").toString())
                                    .transform(new CircleTransform(getApplicationContext()))
                                    .into(img_profile);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    void EventDrawable(){
        pencil_fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFullname.setInputType(InputType.TYPE_CLASS_TEXT);
                pencil_fullname.setImageResource(R.drawable.penci_activel);
                btn_upload_server.setEnabled(true);
            }
        });
        etEmail.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(etEmail) {
            @Override
            public boolean onDrawableClick() {
                etEmail.setInputType(InputType.TYPE_CLASS_TEXT);
                etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.penci_activel, 0);
                btn_upload_server.setEnabled(true);
                return false;
            }
        });

        etPassword.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(etPassword) {
            @Override
            public boolean onDrawableClick() {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.penci_activel, 0);
                btn_upload_server.setEnabled(true);

                return false;
            }
        });

        etPhone.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(etPhone) {
            @Override
            public boolean onDrawableClick() {
                etPhone.setInputType(InputType.TYPE_CLASS_TEXT);
                etPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.penci_activel, 0);
                btn_upload_server.setEnabled(true);
                return false;
            }
        });

        etAddress.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(etAddress) {
            @Override
            public boolean onDrawableClick() {
                etAddress.setInputType(InputType.TYPE_CLASS_TEXT);
                etAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.penci_activel, 0);
                btn_upload_server.setEnabled(true);
                return false;
            }
        });

        etFullname.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(etFullname) {
            @Override
            public boolean onDrawableClick() {
                etFullname.setInputType(InputType.TYPE_CLASS_TEXT);
                etFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.penci_activel, 0);
                btn_upload_server.setEnabled(true);
                return false;
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = String.valueOf(s);
                try{

                    if(txt.subSequence(0,1).equals("*")){

                    }else{
                        pw_disabled = true;

                    }
                }catch (Exception ex){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    Boolean with_image = false;
    void UploadToServer(File file_upload) {
        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(530, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        client.cache();
        final String token_pw2;
        if(!pw_disabled){
            token_pw2 = token_pw;
        }else{
            token_pw2 = etPassword.getText().toString();
        }
        FormBody body = new FormBody.Builder()
                .add("id",id_contact)
                .add("email",etEmail.getText().toString())
                .add("username",etEmail.getText().toString())
                .add("password",token_pw2)
                .add("name",etFullname.getText().toString())
                .add("address",etAddress.getText().toString())
                .add("phone",etPhone.getText().toString())
                .add("is_admin","0")
                .build();

        Request request;
            if(with_image){

                String date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

                RequestBody file_body = RequestBody.create(MediaType.parse(mime), file_upload);
                final String imagename = "image.jpg";
                RequestBody request_body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", imagename, file_body)
                        .addFormDataPart("id",id_contact)
                        .addFormDataPart("email",etEmail.getText().toString())
                        .addFormDataPart("username",etEmail.getText().toString())
                        .addFormDataPart("password",token_pw2)
                        .addFormDataPart("name",etFullname.getText().toString())
                        .addFormDataPart("address",etAddress.getText().toString())
                        .addFormDataPart("phone",etPhone.getText().toString())
                        .addFormDataPart("is_admin","1")
                    .build();
                request = new Request.Builder()
                    .url(base_url_ip + "api/updateuser")
                    .post(request_body)
                        .addHeader("Authorization","Bearer " + TokenSession)
                        .addHeader("Accept","application/json")
                    .build();

            }else{
                request = new Request.Builder()
                        .url(base_url_ip + "api/updateuser")
                        .post(body)
                        .addHeader("Authorization","Bearer " + TokenSession)
                        .addHeader("Accept","application/json")
                        .build();

            }


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responsebody = response.body().string();
                Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jsonfinal = null;
                        Log.d("PROFILE", responsebody);
                        try {
                            pw_disabled = false;
                            with_image = false;
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref_face_detection",0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token_pw",token_pw2);
                            editor.commit();
                            jsonfinal = new JSONObject(responsebody);

                            String res = jsonfinal.getString("success").toString();

                            btn_upload_server.setEnabled(true);
                            btn_upload_server.setText("SIMPAN");
                            new SweetAlertDialog(Profile.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setContentText("Data Berhasil di Ubah:)")
                                    .setConfirmText("Oke")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            MainActivity.ma.getProfile();
                                            //finish();
                                        }
                                    })
                                    .show();



                        } catch (JSONException e) {
                            btn_upload_server.setEnabled(true);

                            btn_upload_server.setText("SIMPAN");
                            new SweetAlertDialog(Profile.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Data Tidak Berhasil diubah :(")
                                    .setContentText(responsebody)
                                    .setConfirmText("Oke")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();

                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void TakeCamera(){
        TypeUpload = "camera";
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
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
    public void Uploadimage(){
        TypeUpload = "upload";
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try{
                ImageUri = data.getData();
                fileimage = new File(getRealPathFromURI(ImageUri));


                ContentResolver cR = getApplicationContext().getContentResolver();
                mime = cR.getType(ImageUri);

                Glide.with(Profile.this)
                        .load(ImageUri)
                        .transform(new CircleTransform(getApplicationContext()))
                        .into(img_profile);

                //save image to server
                with_image = true;
                //UploadToServer(fileimage);
                //Toast.makeText(this, mime, Toast.LENGTH_SHORT).show();

                btn_upload_server.setEnabled(true);
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("eror image", ex.getMessage());
            }


        }else if(resultCode == RESULT_OK && requestCode == PICK_CAMERA){
            try{

                fileimage = new File(getRealPathFromURI(file_camera));
                mime = "image/jpeg";

                Glide.with(Profile.this)
                        .load(file_camera)
                        .transform(new CircleTransform(getApplicationContext()))
                        .into(img_profile);
                with_image = true;

                //UploadToServer(fileimage);
                //Toast.makeText(this, mime, Toast.LENGTH_SHORT).show();

                btn_upload_server.setEnabled(true);
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("test",ex.getMessage());
            }

        }
    }
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

    public static void verifyCameraPermission(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA},100);
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
}
