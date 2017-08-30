package com.example.acer.addword;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class testactivity extends AppCompatActivity {

    Button btnclear, btnskip;

    //boolean permitInput = true;
    int hiddenChar = 0;
    int maxAnswer = 0;
    int currentVocabPosition = 0;
    int correctAnswer = 0;
    Map<Integer, TextView> mapPosition;
    JSONArray orderPosition, vocabularyDataList;
    String currentVocab = "";
    TextView tvscoce, tvTranslate;
    JSONArray alllevel;
    Handler handler;
    int[] second;
    AlertDialog builder;
    SoundUtil sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

        tvscoce = (TextView) findViewById(R.id.tvscoce);
        tvTranslate = (TextView) findViewById(R.id.tvTranslate);
        btnclear = (Button) findViewById(R.id.btnclear);
        btnskip = (Button) findViewById(R.id.btnskip);


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

        if (new PreferenceUtil(this).getCurrentLevel() > 0) {
            Intent in = new Intent(testactivity.this, Playactivity.class);
            in.putExtra("level", new PreferenceUtil(this).getCurrentLevel());
            startActivity(in);
            finish();
            return;
        }

        final String url = getString(R.string.ip_address) + "/addword/action/test-level.php";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String formServer = response.body().string();
                try {
                    JSONObject json = new JSONObject(formServer);
                    if (json.getBoolean("result")) { // if result = true
                        String second = json.getString("second");
                        updateTime(second);


                        final String name = json.getString("name");

                        alllevel = json.getJSONArray("step");
                        hiddenChar = json.getInt("hidden_char");// การ get ค่า json แบบ object
                        maxAnswer = json.getInt("max_answer");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tvname = (TextView) findViewById(R.id.tvname);
                                tvname.setText(name);
                                tvscoce.setText(correctAnswer + " / " + maxAnswer);
                            }
                        });


                        vocabularyDataList = json.getJSONArray("data");
                        displayVocabulary(vocabularyDataList, currentVocabPosition);
                    } else {
                        Toast.makeText(testactivity.this, "ผิดค่ะ ลองคิดใหม่นะค่ะ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void updateTime(final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                second = new int[]{Integer.parseInt(time)};
                TextView tvTime = (TextView) findViewById(R.id.tvTime);
                tvTime.setText(String.valueOf(second[0]));
                handler = new Handler();
                handler.postDelayed(runTime, 1000);
            }
        });

    }

    Runnable runTime = new Runnable() {
        @Override
        public void run() {
            second[0] -= 1;
            if (second[0] >= 0) {
                updateTime(String.valueOf(second[0]));
            } else {
                checkLevel(true);
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
                tvTranslate.setText("(" + transName + ")");
            }
        });
    }

    public ArrayList<Integer> randomHidden(final String vocab) {
        ArrayList<Integer> list = new ArrayList<>();
        Random rand = new Random();
        int hide = vocab.length() <= hiddenChar ? vocab.length() - 1 : hiddenChar;
        do {
            int ind = rand.nextInt(vocab.length() - 1);
            if (!list.contains(ind)) {
                list.add(ind);
            }
        } while (list.size() != hide);
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        char unicodeChar = (char) event.getUnicodeChar();
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            removeVocabulary();
        } else {
            addVocabulary(String.valueOf(unicodeChar));
        }
        return super.onKeyDown(keyCode, event);
    }


    public void addVocabulary(String character) {
        sound = new SoundUtil(this);
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
                        TextView tvResult = (TextView) findViewById(R.id.tvResult);
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
            //currentVocab = "";
            currentVocabPosition += 1;
            correctAnswer += 1;
            tvscoce.setText(correctAnswer + " / " + maxAnswer);

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
                    } catch (Exception ex) {
                    }
                    ;
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
            }, 500);
        } else {
            sound.soundWrong(null);
            // dialog กรณีที่ตอบผิด
            Toast toast = Toast.makeText(testactivity.this, "ตอบผิดคะ กรุณราลองอีกที", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void checkLevel(boolean timeOut) {
        if (correctAnswer == maxAnswer || timeOut) {
            try {
                for (int i = 0; i < alllevel.length(); i++) {
                    JSONObject item = alllevel.getJSONObject(i);
                    int level = Integer.parseInt(item.getString("level_id"));
                    int step = Integer.parseInt(item.getString("number"));
                    if (correctAnswer >= step) {
                        sound.soundPass(null);
                        Intent in = new Intent(testactivity.this, Playactivity.class);
                        in.putExtra("level", 1);
                        startActivity(in);
                        new PreferenceUtil(this).addBonusPoint(level);
                        handler.removeCallbacks(runTime);
                        finish();
                        break;
                    }
                    if (i == (alllevel.length() - 1)) {
                        sound.soundPass(null);
                        new PreferenceUtil(this).addBonusPoint(1);
                        Intent in = new Intent(testactivity.this, Playactivity.class);
                        in.putExtra("level", 1);
                        startActivity(in);
                        handler.removeCallbacks(runTime);
                        finish();
                        break;

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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

