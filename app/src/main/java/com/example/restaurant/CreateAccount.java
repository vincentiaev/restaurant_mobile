package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class CreateAccount extends AppCompatActivity {

    TextView cr_username, cr_password, cr_password_confirm, cr_nama, cr_alamat, cr_hp;
    Button cr_account, cr_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        cr_account = findViewById(R.id.cr_account);
        cr_login = findViewById(R.id.cr_login);

        cr_username = findViewById(R.id.cr_username);
        cr_password = findViewById(R.id.cr_password);
        cr_password_confirm = findViewById(R.id.cr_password_confirm);
        cr_nama = findViewById(R.id.cr_nama);
        cr_alamat = findViewById(R.id.cr_alamat);
        cr_hp = findViewById(R.id.cr_hp);

        cr_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                if (cr_username.getText().toString().trim().isEmpty()){
                    cr_username.setError("Username harus diisi");
                    valid = false;
                }
                if (cr_password.getText().toString().trim().isEmpty()){
                    cr_password.setError("Password harus diisi");
                    valid = false;
                }
                if (cr_password_confirm.getText().toString().trim().isEmpty()){
                    cr_password_confirm.setError("Password confirm harus diisi");
                    valid = false;
                }
                if (!cr_password.getText().toString().equals(cr_password_confirm.getText().toString())){
                    cr_password_confirm.setError("Confirm password tidak sama");
                    valid = false;
                }
                if (cr_nama.getText().toString().trim().isEmpty()){
                    cr_nama.setError("Nama harus diisi");
                    valid = false;
                }
                if (cr_alamat.getText().toString().trim().isEmpty()){
                    cr_alamat.setError("Alamat harus diisi");
                    valid = false;
                }
                if (cr_hp.getText().toString().trim().isEmpty()){
                    cr_hp.setError("Nomor HP harus diisi");
                    valid = false;
                }
                if (valid){
                    new createAccount().execute();
                }
            }
        });

        cr_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

    }


    private class createAccount extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();

            //login
            String u = "http://192.168.26.97:8000/create_account";

            ArrayList<String> mykey = new ArrayList<String>();
            ArrayList myvalue = new ArrayList<>();

            mykey.add("username");
            myvalue.add(cr_username.getText());
            mykey.add("password");
            myvalue.add(cr_password.getText());
            mykey.add("nama");
            myvalue.add(cr_nama.getText());
            mykey.add("alamat");
            myvalue.add(cr_alamat.getText());
            mykey.add("hp");
            myvalue.add(cr_hp.getText());

            String s = h.postAccess(u, mykey, myvalue);
            Log.d("REST01", "Result cr acc: " + s);

            if (s!=null){
                try {
                    JSONObject jo = new JSONObject(s);

                    int status = jo.getInt("status");
                    if (status == 200) {
                        showPopup("Berhasil membuat akun. Silakan login!");
                    } else if (status == 403){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cr_username.setError("Username tidak tersedia");
                            }
                        });
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