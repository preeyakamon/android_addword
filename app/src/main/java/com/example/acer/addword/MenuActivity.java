package com.example.acer.addword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    ImageView imgstart, imgsta, imgsetting;
    Button btnlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        imgstart = (ImageView)findViewById(R.id.imgstart);
        imgsta = (ImageView)findViewById(R.id.imgsta);
        imgsetting = (ImageView)findViewById(R.id.imgsetting);
        btnlogout = (Button) findViewById(R.id.btnlogout);

        imgsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, statisticActivity.class);
                startActivity(intent);
            }
        });

        imgstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, testactivity.class);
                startActivity(intent);
            }
        });

        imgsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, edituseractivity.class);
                startActivity(intent);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PreferenceUtil(MenuActivity.this).removeAllSession();
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
