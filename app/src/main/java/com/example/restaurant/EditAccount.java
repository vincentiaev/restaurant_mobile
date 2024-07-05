package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditAccount extends AppCompatActivity {

    String user_id, nama, password, username, alamat, hp;
    TextView cr_username, cr_password, cr_password_confirm, cr_nama, cr_alamat, cr_hp, updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        cr_username = findViewById(R.id.cr_username);
        cr_nama = findViewById(R.id.cr_nama);
        cr_alamat = findViewById(R.id.cr_alamat);
        cr_hp = findViewById(R.id.cr_hp);

        updateButton = findViewById(R.id.updateButton);

        Intent i = getIntent();
        user_id = String.valueOf(i.getIntExtra("user_id", 0));
        nama = i.getStringExtra("nama");
        password = i.getStringExtra("password");
        username = i.getStringExtra("username");
        alamat = i.getStringExtra("alamat");
        hp = i.getStringExtra("hp");

        cr_username.setText(username);
        cr_nama.setText(nama);
        cr_alamat.setText(alamat);
        cr_hp.setText(hp);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateAccount().execute();
            }
        });
    }
    private class updateAccount extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();

            // Update account
            String u = "http://192.168.26.97:8000/update/" + user_id;

            ArrayList<String> mykey = new ArrayList<>();
            ArrayList<String> myvalue = new ArrayList<>();

            mykey.add("nama");
            myvalue.add(cr_nama.getText().toString());
            mykey.add("alamat");
            myvalue.add(cr_alamat.getText().toString());
            mykey.add("hp");
            myvalue.add(cr_hp.getText().toString());

            String s = h.putAccess(u, mykey, myvalue);
            Log.d("REST01", "Result update acc: " + s);
            Log.d("REST01", String.valueOf(mykey)+myvalue);

            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);

                    int status = jo.getInt("status");
                    if (status == 200) {
                        showPopup("Berhasil mengupdate akun");
                    } else if (status == 403) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showPopup("Update gagal, periksa kembali data Anda.");
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