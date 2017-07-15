package com.example.acer.addword;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    int level = 1;
    boolean permitInput = true;
    int hiddenChar = 0;
    int numberanswer = 0;
    int maxAnswer = 0;
    int correctAnswer = 0;
    int currentVocabPosition = 0;
    Map<Integer, TextView> mapPosition;
    JSONArray orderPosition, vocabularyDataList;
    String currentVocab = "";
    TextView tvscoce_p;
    JSONArray allLevel;
    Handler handler;
    int[] second;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playactivity);

        tvscoce_p = (TextView) findViewById(R.id.tvscoce_p);

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

    public void updateTime(final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                second = new int[]{Integer.parseInt(time)};
                TextView tvTime = (TextView) findViewById(R.id.tv_pTime);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                TextView translate = new TextView(Playactivity.this);
                translate.setTextSize(26);
                translate.setText(String.format(" (%s)", transName));
                layoutShuffle.addView(translate);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        char unicodeChar = (char) event.getUnicodeChar();
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            permitInput = true;
            removeVocabulary();
        } else {
            if (permitInput) {
                addVocabulary(String.valueOf(unicodeChar));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addVocabulary(String character) {
        SortedSet<Integer> keys = new TreeSet<>(mapPosition.keySet());
        for (Integer key : keys) {
            TextView tv = mapPosition.get(key);
            String value = tv.getText().toString();
            if (value.equalsIgnoreCase("")) {
                tv.setText(character.toUpperCase());
                orderPosition.put(key);
                checkAnswer(character, key);
                checkNewVocab();
                break;
            }
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
                        TextView tv_pResult = (TextView) findViewById(R.id.tv_pResult);
                        tv_pResult.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void checkAnswer(final String myCharacter, final int position) {
        TextView tv_pResult = (TextView) findViewById(R.id.tv_pResult);
        tv_pResult.setTextSize(50);

        final String correct = String.valueOf(currentVocab.charAt(position));
        if (myCharacter.equalsIgnoreCase(correct)) {
            tv_pResult.setText("O");
            permitInput = true;
        } else {
            tv_pResult.setText("X");
            permitInput = false;
        }

    }

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
            mapPosition.clear();
            orderPosition = new JSONArray();
            currentVocab = "";
            currentVocabPosition += 1;
            correctAnswer += 1;
            new PreferenceUtil(this).plusPointByLevel(level);
            tvscoce_p.setText(correctAnswer + " / " + maxAnswer);
            checkLevel(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        displayVocabulary(vocabularyDataList, currentVocabPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, 3000);

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
                    Log.d("PlayActivityLog", "go to: " + hasLevel);
                    Intent in = new Intent(this, Playactivity.class);
                    in.putExtra("level", hasLevel);
                    handler.removeCallbacks(runTime);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(this, DisplaySummary.class);
                    startActivity(in);
                    handler.removeCallbacks(runTime);
                    finish();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
