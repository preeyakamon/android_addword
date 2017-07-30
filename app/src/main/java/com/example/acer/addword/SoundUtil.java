package com.example.acer.addword;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by ACER on 7/29/2017.
 */

public class SoundUtil {

    private Context ctx;
    private MediaPlayer mp;

    public SoundUtil(Context c) {
        ctx = c;
    }

    public void play() {
        if (mp != null) {
            mp.start();
        }
    }

    public void stop() {
        if (mp != null) {
            mp.stop();
        }
    }

    public void soundWrong(final SoundListener listener) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
        mp = MediaPlayer.create(ctx, R.raw.wrong);
        play();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)
                listener.onSoundPlayed();
            }
        });
    }

    public void soundCorrect(final SoundListener listener) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
        mp = MediaPlayer.create(ctx, R.raw.correct);
        play();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)
                listener.onSoundPlayed();
            }
        });
    }

    public void soundPass(final SoundListener listener) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
        mp = MediaPlayer.create(ctx, R.raw.past);
        play();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)
                listener.onSoundPlayed();
            }
        });
    }

    public void soundEnd(final SoundListener listener) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
        mp = MediaPlayer.create(ctx, R.raw.end);
        play();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)
                listener.onSoundPlayed();
            }
        });
    }

    public void soundSplash(final SoundListener listener) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
        mp = MediaPlayer.create(ctx, R.raw.splash);
        play();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)
                listener.onSoundPlayed();
            }
        });
    }

    public void soundButton(final SoundListener listener) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
        mp = MediaPlayer.create(ctx, R.raw.button);
        play();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)
                listener.onSoundPlayed();
            }
        });
    }

}
