package com.example.acer.addword;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Playactivity extends AppCompatActivity {

    Button btnclear, btnskip;

    int level = 1;
    int hiddenChar = 0;
    int numberanswer = 0;
    int maxAnswer = 0;
    int correctAnswer = 0;
    int currentVocabPosition = 0;
    Map<Integer, TextView> mapPosition;
    JSONArray orderPosition, vocabularyDataList;
    String currentVocab = "";
    TextView tvscoce_p, tvTranslate;
    JSONArray allLevel;
    //Handler handler;
    int[] second;
    AlertDialog builder;
    SoundUtil sound;
    private ProgressBar progressBar;
    private int time = 0;
    int currentTime = 0;

    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (currentTime < time) {
                currentTime += 1;
                startCountDown();
                handler.postDelayed(runnable, 1000);
            } else {
                checkLevel(true);
                handler.removeCallbacks(runnable);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playactivity);

        tvscoce_p = (TextView) findViewById(R.id.tvscoce_p);
        tvTranslate = (TextView) findViewById(R.id.tvTranslate);
        btnclear = (Button) findViewById(R.id.btnclear);
        btnskip = (Button) findViewById(R.id.btnskip);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));


        sound = new SoundUtil(this);

        btnskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPosition.clear();
                orderPosition = new JSONArray();
                currentVocab = "";
                currentVocabPosition += 1;
                try {
                    displayVocabulary(vocabularyDataList, currentVocabPosition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderPosition != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < orderPosition.length(); i++) {
                                TextView tv = null;
                                try {
                                    tv = mapPosition.get(orderPosition.getInt(i));
                                    tv.setText("");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            orderPosition = new JSONArray();
                        }
                    });
                }
            }
        });

        mapPosition = new HashMap<>();
        orderPosition = new JSONArray();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            level = bundle.containsKey("level") ? bundle.getInt("level") : 1;
            new PreferenceUtil(this).setCurrentLevel(level);
        }

        correctAnswer = new PreferenceUtil(this).getPointByLevel(level);

        final String url = getString(R.string.ip_address) + "/addword/action/level.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("level_id", String.valueOf(level))
                .build();
        Request request = new Request.Builder()//การเตรียมข้อมูลเพื่อเรียกใช้
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String formServer = response.body().string();
                try {
                    JSONObject json = new JSONObject(formServer);
                    if (json.getBoolean("result")) { // if result = true
                        String second = json.getString("second");
                        time = Integer.parseInt(second);
                        updateTime(second);

                        final String name = json.getString("name");

                        allLevel = json.getJSONArray("step");
                        maxAnswer = json.getInt("number_answer");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tvname_p = (TextView) findViewById(R.id.tvname_p);
                                tvname_p.setText(name);
                                tvscoce_p.setText(correctAnswer + " / " + maxAnswer);
                            }
                        });


                        hiddenChar = json.getInt("hidden_char");// การ get ค่า json แบบ object
                        numberanswer = json.getInt("number_answer");

                        vocabularyDataList = json.getJSONArray("data");
                        displayVocabulary(vocabularyDataList, currentVocabPosition);


                    } else {
                        Toast.makeText(Playactivity.this, "ผิดค่ะ ลองคิดใหม่นะค่ะ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    public void startCountDown() {
        float percentage = (float) ((currentTime * 100) / time);
        final float progress = (float) (100.0 - percentage);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvTime = (TextView) findViewById(R.id.tv_pTime);
                tvTime.setText(String.valueOf(time - currentTime));
                if (progress <= 30) {
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    new PreferenceUtil(Playactivity.this).saveTotalTime();
                }
                progressBar.setProgress((int) progress);
            }
        });
    }

    public void updateTime(final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ///second = new int[]{Integer.parseInt(time)};
                //TextView tvTime = (TextView) findViewById(R.id.tv_pTime);
               // tvTime.setText(String.valueOf(second[0]));
               // handler = new Handler();
                handler.postDelayed(runTime, 1000);
            }
        });

    }

    Runnable runTime = new Runnable() {
        @Override
        public void run() {
            if (currentTime < time) {
                currentTime += 1;
                startCountDown();
                handler.postDelayed(runTime, 1000);
            } else {
                checkLevel(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(Playactivity.this);
                builder.setMessage("แย่จัง!! หมดเวลาแล้ว เล่นใหม่นะ ")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Playactivity.this, MenuActivity.class);
                                startActivity(intent);
                                new PreferenceUtil(Playactivity.this).clearSession();
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

    public void displayVocabulary(JSONArray vocab, int position) throws JSONException {
        final LinearLayout layoutShuffle = (LinearLayout) findViewById(R.id.layoutShuffle);

        layoutShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSoftKeyboard();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTranslate.setText("");
                layoutShuffle.removeAllViews();
            }
        });
        JSONObject item = vocab.getJSONObject(position);
        final String vocabName = item.getString("vocab_name");
        final String transName = item.getString("translation");
        currentVocab = vocabName;
        ArrayList<Integer> hidden = randomHidden(vocabName);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.setMargins(4, 0, 4, 0);
        for (int j = 0; j < vocabName.length(); j++) {
            char txt = vocabName.charAt(j);
            final TextView tv = new TextView(this);
            tv.setTextSize(26);
            tv.setLayoutParams(llp);
            if (!hidden.contains(j)) {
                tv.setText(String.valueOf(txt));
            } else {
                tv.setBackground(getResources().getDrawable(R.drawable.bottom_line));
                mapPosition.put(j, tv);
            }
            tv.setMinWidth(40);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutShuffle.addView(tv);
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTranslate.setText("("+transName+")");
            }
        });
    }

    public ArrayList<Integer> randomHidden(final String vocab) {
        ArrayList<Integer> list = new ArrayList<>();
        Random rand = new Random();
        int hide = vocab.length() <= hiddenChar ? vocab.length() - 1 : vocab.length();
        do {
            int ind = rand.nextInt(hide - 1);
            if (!list.contains(ind)) {
                list.add(ind);
            }
        } while (list.size() != hiddenChar);
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//แป้นพิมพ์
        char unicodeChar = (char) event.getUnicodeChar();
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            removeVocabulary();
        } else {
            addVocabulary(String.valueOf(unicodeChar));
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addVocabulary(String character) {
        sound.soundButton(null);
        SortedSet<Integer> keys = new TreeSet<>(mapPosition.keySet());
        for (Integer key : keys) {
            TextView tv = mapPosition.get(key);
            String value = tv.getText().toString();
            if (value.equalsIgnoreCase("")) {
                tv.setText(character.toUpperCase());
                orderPosition.put(key);
                //checkAnswer(character, key);
                break;
            }
        }

        if (orderPosition.length() == mapPosition.size()) {
            checkNewVocab();
        }
    }

    public void removeVocabulary() {
        if (orderPosition != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = null;
                    try {
                        tv = mapPosition.get(orderPosition.getInt(orderPosition.length() - 1));
                        tv.setText("");
                        orderPosition.remove(orderPosition.length() - 1);
                        TextView tvResult = (TextView) findViewById(R.id.tv_pResult);
                        tvResult.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

//    public void checkAnswer(final String myCharacter, final int position) {
//        TextView tvResult = (TextView) findViewById(R.id.tvResult);
//        tvResult.setTextSize(50);
//
//        final String correct = String.valueOf(currentVocab.charAt(position));
//        if (myCharacter.equalsIgnoreCase(correct)) {
//            tvResult.setText("O");
//        } else {
//            tvResult.setText("X");
//        }
//    }

    public void checkNewVocab() {
        boolean complete = false;
        SortedSet<Integer> keys = new TreeSet<>(mapPosition.keySet());
        for (Integer key : keys) {
            TextView tv = mapPosition.get(key);
            String answer = tv.getText().toString();
            String correct = String.valueOf(currentVocab.charAt(key));
            if (answer.equalsIgnoreCase(correct)) {
                complete = true;
            } else {
                complete = false;
                break;
            }
        }
        if (complete) {
            sound.soundCorrect(null);
            mapPosition.clear();
            orderPosition = new JSONArray();

            currentVocabPosition += 1;
            correctAnswer += 1;
            new PreferenceUtil(this).plusPointByLevel(level);
            tvscoce_p.setText(correctAnswer + " / " + maxAnswer);

            builder = new AlertDialog.Builder(this).create();
            View view = View.inflate(this, R.layout.custom_dialog_correct, null);
            TextView tv = (TextView) view.findViewById(R.id.tvVocab);
            tv.setText(currentVocab);
            builder.setView(view);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (this != null) builder.show();
                    } catch (Exception ex) {};
                }
            });
            currentVocab = "";
            checkLevel(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (this != null) builder.dismiss();
                        displayVocabulary(vocabularyDataList, currentVocabPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        } else {
            sound.soundWrong(null);
            // dialog กรณีที่ตอบผิด
            Toast toast = Toast.makeText(Playactivity.this, "ตอบผิดคะ กรุณราลองอีกที", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void checkLevel(boolean b) {
        try {
            if (correctAnswer == maxAnswer) {
                int hasLevel = level + 1;
                boolean upLevel = false;
                for (int i = 0; i < allLevel.length(); i++) {
                    JSONObject item = allLevel.getJSONObject(i);
                    int level_id = Integer.parseInt(item.getString("level_id"));
                    if (level_id == hasLevel) {
                        upLevel = true;
                        break;
                    }
                }

                if (upLevel == true) {
                    sound.soundPass(null);

                    //ใส่ไดอาล็อคตรงนี้
                    Log.d("PlayActivityLog", "go to: " + hasLevel);
                    Intent in = new Intent(this, Playactivity.class);
                    in.putExtra("level", hasLevel);
                    handler.removeCallbacks(runTime);
                    startActivity(in);
                    builder.dismiss();
                    finish();
                } else {
                    sound.soundPass(null);
                    Intent in = new Intent(this, DisplaySummary.class);
                    startActivity(in);
                    handler.removeCallbacks(runTime);

                    builder.dismiss();
                    finish();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void openSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runTime);
    }
}

