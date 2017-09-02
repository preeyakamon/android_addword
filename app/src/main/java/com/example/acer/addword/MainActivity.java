package com.example.acer.addword;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    EditText etusername, etpassword;
    Button btnlogin, btnregister;
    TextView txtforget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etusername = (EditText) findViewById(R.id.etusername);
        etpassword = (EditText) findViewById(R.id.etpassword);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnregister = (Button) findViewById(R.id.btnregister);
        txtforget =(TextView)findViewById(R.id.txtforget);

        String tempString="ลืมรหัสผ่าน";
        TextView text=(TextView)findViewById(R.id.txtforget);
        SpannableString spanString = new SpannableString(tempString);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        text.setText(spanString);




        if (new PreferenceUtil(this).getUserLogin() != null) {
            Log.d("TAG", "onCreate: " + new PreferenceUtil(this).getUserLogin());
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }

        txtforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.custom_forger_password, null);
                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etusername = (EditText) view.findViewById(R.id.etusername);
                                String url = getString(R.string.ip_address) + "/addword/action/sendemail.php";

                                OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()
                                        .add("username", etusername.getText().toString())
                                        .build();
                                Request request = new Request.Builder()//การเตรียมข้อมูลเพื่อเรียกใช้
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
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                             }catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        }).show();
               }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etusername.getText().toString();
                String password = etpassword.getText().toString();
                String url = getString(R.string.ip_address) + "/addword/action/login.php";

                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()//การเตรียมข้อมูลเพื่อเรียกใช้
                        .url(url)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {// ตัวเรียก เซอร์วิส
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

                                // session.
                                new PreferenceUtil(MainActivity.this).saveUserIDLogin(json.getInt("id"));
                                new PreferenceUtil(MainActivity.this).saveUserLogin(json.getString("username"));

                                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "ข้อมูลผิดพลาด กรุณากรอกอีกครั้ง", Toast.LENGTH_SHORT).show();
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
