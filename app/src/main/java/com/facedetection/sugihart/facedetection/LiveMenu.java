package com.facedetection.sugihart.facedetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LiveMenu extends AppCompatActivity {
    Button btnscene1,btnscen2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_menu);
        try{
            btnscene1 = (Button) findViewById(R.id.btnscene1);
            btnscen2 = (Button) findViewById(R.id.btnscene2);
            btnscene1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in= new Intent(getApplicationContext(),LiveStreaming.class);
                    in.putExtra("url","http://sdinuc01.ddns.net:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/jbP7Zj87oU/s.mp4");
                    startActivity(in);

                }
            });
            btnscen2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in= new Intent(getApplicationContext(),LiveStreaming.class);
                    in.putExtra("url","http://sdinuc01.ddns.net:8080/CVVotFBf6mmTAksEt8vfxqL7v5aAUi/mp4/H3naAOyoiW/GXMVvFfuGa/s.mp4");
                    startActivity(in);

                }
            });
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
