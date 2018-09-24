package com.facedetection.sugihart.facedetection;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.VideoView;

public class LiveStreaming extends AppCompatActivity {
    VideoView videoView;
    public static LiveStreaming ls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_streaming);
        videoView = (VideoView) findViewById(R.id.video_view);
        ls = this;
        try{
            getSupportActionBar().setTitle("Live Streaming");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //RtspStream();
        getWebView(getIntent().getStringExtra("url").toString());
    }
    public void getWebView(String url){

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
    public void RtspStream() {
        videoView.setVideoURI(Uri.parse("http://122844270777.ip-dynamic.com:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/jbP7Zj87oU/s.mp4"));
        videoView.requestFocus();
        videoView.start();
    }
}
