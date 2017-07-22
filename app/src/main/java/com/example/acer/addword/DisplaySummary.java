package com.example.acer.addword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplaySummary extends AppCompatActivity {

    JSONArray alllevel;
    TextView tvUsername, tvScore, tvTime;
    int totalPoint, levelMax;
    Button btnmenu, btnsta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_summary);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvTime = (TextView) findViewById(R.id.tvTime);
        int totalTime = new PreferenceUtil(this).getTotalTime();
        tvTime.setText(String.valueOf(totalTime));
        btnmenu = (Button) findViewById(R.id.btnmenu);
        btnsta = (Button) findViewById(R.id.btnsta);

        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplaySummary.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

      btnsta.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(DisplaySummary.this, statisticActivity.class);
              startActivity(intent);
              finish();
          }
      });

        tvUsername.setText(new PreferenceUtil(this).getUserLogin());

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
                        alllevel = json.getJSONArray("step");
                        validatePoint();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void validatePoint() {
        try {
            totalPoint = 0;
            levelMax = 1;
            int bonus = new PreferenceUtil(this).getBonusPoint();
            for (int i = 0; i < alllevel.length(); i++) {
                JSONObject item = alllevel.getJSONObject(i);
                int level = Integer.parseInt(item.getString("level_id"));
                levelMax = level;
                int point = new PreferenceUtil(this).getPointByLevel(level);
                Log.d("StatisLog", "point: " + point + " on level: " + level);
                if (bonus == level) {
                    totalPoint = totalPoint + (point * bonus);
                } else {
                    totalPoint = totalPoint + point;
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("StatisLog", "total: " + totalPoint);
                    prepareToSend(totalPoint, levelMax);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void prepareToSend(int score, int maxLevel) {
        int userID = new PreferenceUtil(this).getUserIDLogin();
        int totalTime = new PreferenceUtil(this).getTotalTime();
        int mScore = new Integer(score);
        int mLevel = new Integer(maxLevel);
        tvScore.setText(String.valueOf(mScore));

        final String url = getString(R.string.ip_address) + "/addword/action/statistic.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("user_id", String.valueOf(userID))
                .add("score", String.valueOf(mScore))
                .add("level_id", String.valueOf(mLevel))
                .add("totaltime", String.valueOf(totalTime))
                .build();
        Request request = new Request.Builder()//การเตรียมข้อมูลเพื่อเรียกใช้
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new PreferenceUtil(DisplaySummary.this).clearSession();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                new PreferenceUtil(DisplaySummary.this).clearSession();
            }
        });
    }
}
