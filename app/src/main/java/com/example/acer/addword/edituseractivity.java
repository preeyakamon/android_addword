package com.example.acer.addword;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import static com.example.acer.addword.PreferenceUtil.getUserIDLogin;
import static com.example.acer.addword.R.id.etnewpassword;
import static com.example.acer.addword.R.id.tvUsername;

public class edituseractivity extends AppCompatActivity {

    Button btnsave, btncancel, btndel;
    EditText etname, etpassword, etnewpassword, etconfirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edituseractivity);

        etname = (EditText) findViewById(R.id.etname);
        etpassword = (EditText) findViewById(R.id.etpassword);
        etnewpassword = (EditText) findViewById(R.id.etnewpassword);
        etconfirmpassword = (EditText) findViewById(R.id.etconfirmpassword);
        btnsave = (Button) findViewById(R.id.btnsave);
        btncancel = (Button) findViewById(R.id.btncancel);
        btndel = (Button) findViewById(R.id.btndel);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userid = new PreferenceUtil(edituseractivity.this).getUserIDLogin();
                String name = etname.getText().toString();
                String password = etpassword.getText().toString();
                final String newpassword = etnewpassword.getText().toString();
                String confirmpassword = etconfirmpassword.getText().toString();
                String url = getString(R.string.ip_address) + "/addword/action/androidedituser.php";

                if (newpassword.equals(confirmpassword))
                    Toast.makeText(getApplicationContext(), "แก้ไขข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getApplicationContext(), "Password ไม่ตรงกัน กรุณาใส่ใหม่", Toast.LENGTH_SHORT).show();
                    return;
                }


                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("user_id", String.valueOf(userid))
                        .add("name", name)
                        .add("password", password)
                        .add("newpassword", newpassword)
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
                        new PreferenceUtil(edituseractivity.this).clearSession();
                        Intent intent = new Intent(edituseractivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                });

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = edituseractivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.custom_remove_confirm, null);
                new AlertDialog.Builder(edituseractivity.this)
                        .setView(view)
                        .setPositiveButton("ลบข้อมูล", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    final EditText etPass = (EditText) view.findViewById(R.id.etPass);
                                    final String inputPass = etPass.getText().toString();
                                    int userid = new PreferenceUtil(edituseractivity.this).getUserIDLogin();
                                    String password = etPass.getText().toString();
                                    String url = getString(R.string.ip_address) + "/addword/action/androiduserdel.php";

                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody body = new FormBody.Builder()
                                            .add("user_id", String.valueOf(userid))
                                            .add("password", password)
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
                                            try {
                                                final JSONObject obj = new JSONObject(response.body().string());
                                                if (obj.has("result")) {
                                                    if (obj.getBoolean("result")) {
                                                        new PreferenceUtil(edituseractivity.this).removeAllSession();
                                                        startActivity(new Intent(edituseractivity.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    Toast.makeText(edituseractivity.this, obj.getString("ลบบัญชีผู้ใช้เรียบร้อยแล้ว"), Toast.LENGTH_SHORT).show();
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
    }
}
