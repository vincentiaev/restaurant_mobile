package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Login extends AppCompatActivity {

    TextView username_et, password_et;
    Button login_bt, create_acc_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        login_bt = findViewById(R.id.login_bt);
        create_acc_bt = findViewById(R.id.create_acc_bt);

        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new login().execute();
            }
        });

        create_acc_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreateAccount.class);
                startActivity(i);
            }
        });
    }

    private class login extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();

            //login
            String u = "http://192.168.26.97:8000/login";

            ArrayList<String> mykey = new ArrayList<String>();
            ArrayList myvalue = new ArrayList<>();

            mykey.add("username");
            myvalue.add(username_et.getText());
            mykey.add("password");
            myvalue.add(password_et.getText());

            String s = h.postAccess(u, mykey, myvalue);
            Log.d("REST01", "Result: " + s);

            if (s!=null){
                try {
                    JSONObject jo = new JSONObject(s);

                    int status = jo.getInt("status");
                    if (status == 200) {
                        JSONArray responseArray = jo.getJSONArray("response");
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject userObject = responseArray.getJSONObject(j);

                            int user_id = userObject.getInt("user_id");
                            String username = userObject.getString("username");
                            String password = userObject.getString("password");
                            String nama = userObject.getString("nama");
                            String alamat = userObject.getString("alamat");
                            String hp = userObject.getString("hp");

                            Log.d("REST01", "User ID: " + user_id);
                            Log.d("REST01", "Username: " + username);
                            Log.d("REST01", "Password: " + password);
                            Log.d("REST01", "Nama: " + nama);
                            Log.d("REST01", "Alamat: " + alamat);
                            Log.d("REST01", "HP: " + hp);

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra("user_id", user_id);
                            i.putExtra("username", username);
                            i.putExtra("nama", nama);
                            i.putExtra("password", password);
                            i.putExtra("alamat", alamat);
                            i.putExtra("hp", hp);
                            startActivity(i);
                        }
                    } else if (status == 403){
                        showPopup("Username atau password salah");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }
    }
    private void showPopup(String message) {
        PopUpFragment popupFragment = PopUpFragment.newInstance(message);
        popupFragment.show(getSupportFragmentManager(), "PopupFragment");
    }

}