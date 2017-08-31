package com.example.acer.addword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class statisticActivity extends AppCompatActivity {

    EditText etsearch;
    ImageView imgsea;
    String url;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        etsearch = (EditText) findViewById(R.id.etsearch);
        imgsea = (ImageView) findViewById(R.id.imgsea);
        url = getString(R.string.ip_address) + "/addword/action/seachstatistic.php";

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        showAll();

        imgsea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etsearch.getText().toString();

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("search", keyword)
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
                            final JSONArray jsonArray = new JSONArray(formServer);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new StatisticAdapter(statisticActivity.this, jsonArray);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public void showAll() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("search", "")
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
                    final JSONArray jsonArray = new JSONArray(formServer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new StatisticAdapter(statisticActivity.this, jsonArray);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
