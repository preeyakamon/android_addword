package com.example.acer.addword;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView imageLogo;
    SoundUtil sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        sound = new SoundUtil(this);
        sound.soundSplash(new SoundListener() {
            @Override
            public void onSoundPlayed() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        });

        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        imageLogo.setAnimation(fadein);

    }
}
