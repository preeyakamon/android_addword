package com.example.acer.addword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class registerActivity extends AppCompatActivity {

    EditText etname, etusername, etpassword, etconfirm, etemail;
    Button btnregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        etname = (EditText)findViewById(R.id.etname);
        etconfirm = (EditText)findViewById(R.id.etconfirm);
        etusername = (EditText)findViewById(R.id.etusername);
        etpassword = (EditText)findViewById(R.id.etpassword);
        etemail =(EditText) findViewById(R.id.etemail);
        btnregister = (Button)findViewById(R.id.btnregister);


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etname.getText().toString();
                String username = etusername.getText().toString();
                String password = etpassword.getText().toString();
                String confirm = etconfirm.getText().toString();
                String email = etemail.getText().toString();

                String url = getString(R.string.ip_address) + "/addword/action/registeruser.php";

                if (password.equals(confirm)) {
                   // Toast.makeText(getApplicationContext(), "Matches", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Password ไม่ตรงกัน กรุณากรอกใหม่", Toast.LENGTH_SHORT).show();
                    return;
                }

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("name", name)
                        .add("username", username)
                        .add("password", password)
                        .add("email", email)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
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
                            final JSONObject json = new JSONObject(formServer);
                            Log.d("TAG", "onResponse: " + json);
                            if (json.getBoolean("result")) { // if result = true
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            onBackPressed();
                                            Toast.makeText(registerActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(registerActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                        }catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        }
                                });
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}
