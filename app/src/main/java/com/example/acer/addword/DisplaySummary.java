package com.example.acer.addword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplaySummary extends AppCompatActivity {

    JSONArray alllevel;
    TextView tvUsername, tvScore;
    int totalPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisticactivity);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvScore = (TextView) findViewById(R.id.tvScore);

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
            int bonus = new PreferenceUtil(this).getBonusPoint();
            for (int i = 0; i < alllevel.length(); i++) {
                JSONObject item = alllevel.getJSONObject(i);
                int level = Integer.parseInt(item.getString("level_id"));
                int point = new PreferenceUtil(this).getPointByLevel(level);
                if (bonus == level) {
                    totalPoint = totalPoint + (point * bonus);
                } else {
                    totalPoint = totalPoint + point;
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvScore.setText(String.valueOf(totalPoint));
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
