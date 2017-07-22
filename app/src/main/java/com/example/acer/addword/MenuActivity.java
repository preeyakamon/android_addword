package com.example.acer.addword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    ImageView imgstart, imgremark, imgsetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        imgstart = (ImageView)findViewById(R.id.imgstart);
        imgremark = (ImageView)findViewById(R.id.imgremark);
        imgsetting = (ImageView)findViewById(R.id.imgsetting);

        imgremark.setOnClickListener(new View.OnClickListener() {
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
    }
}
