package com.example.acer.addword;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class testactivity extends AppCompatActivity {

    boolean permitInput = true;
    int hiddenChar = 0;
    int maxAnswer = 0;
    int currentVocabPosition = 0;
    Map<Integer, TextView> mapPosition;
    JSONArray orderPosition, vocabularyDataList;
    String currentVocab = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

        mapPosition = new HashMap<>();
        orderPosition = new JSONArray();

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
                try{
                    JSONObject json = new JSONObject(formServer);
                    if (json.getBoolean("result")) { // if result = true
                        String second = json.getString("second");
                        updateTime(second);

                        hiddenChar = json.getInt("hidden_char");// การ get ค่า json แบบ object
                        maxAnswer = json.getInt("max_answer");

                        vocabularyDataList  = json.getJSONArray("data");
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
    public void updateTime(final String time){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int[] second = {Integer.parseInt(time)};
                TextView tvTime = (TextView) findViewById(R.id.tvTime);
                tvTime.setText(String.valueOf(second[0]));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        second[0] -=1;
                        if (second[0] >=0){
                            updateTime(String.valueOf(second[0]));
                        }
                    }
                },1000 );
            }
        });

    }

    public void displayVocabulary(JSONArray vocab,int position) throws JSONException {
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
            }else {
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
                TextView translate = new TextView(testactivity.this);
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
    public boolean onKeyDown(int keyCode, KeyEvent event){
        char unicodeChar = (char) event.getUnicodeChar();
        if(keyCode == KeyEvent.KEYCODE_DEL){
            permitInput = true;
            removeVocabulary();
        }else {
            if (permitInput){
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
    public void removeVocabulary(){
        if (orderPosition != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = null;
                    try{
                        tv = mapPosition.get(orderPosition.getInt(orderPosition.length() -1));
                        tv.setText("");
                        orderPosition.remove(orderPosition.length() -1);
                        TextView tvResult = (TextView) findViewById(R.id.tvResult);
                        tvResult.setText("");
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public void checkAnswer(final String myCharacter, final int position) {
        TextView tvResult = (TextView) findViewById(R.id.tvResult);
        tvResult.setTextSize(50);

        final String correct = String.valueOf(currentVocab.charAt(position));
        if (myCharacter.equalsIgnoreCase(correct)) {
            tvResult.setText("O");
            permitInput = true;
        } else {
            tvResult.setText("X");
            permitInput = false;
        }

    }
    public void checkNewVocab(){
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
            if (complete){
                mapPosition.clear();
                orderPosition = new JSONArray();
                currentVocab = "";
                currentVocabPosition +=1;
                try {
                    displayVocabulary(vocabularyDataList, currentVocabPosition);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

