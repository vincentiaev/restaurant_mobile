package com.example.restaurant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFragment extends Fragment {

    String nama, password, username, alamat, hp;
    Integer user_id;
    TextView nama_user, hp_user, alamat_user;
    LinearLayout history_button, logout_button;
    ImageView edit_icon;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(Integer user_id, String nama, String username, String password, String alamat, String hp) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", user_id);
        args.putString("nama", nama);
        args.putString("username", username);
        args.putString("password", password);
        args.putString("alamat", alamat);
        args.putString("hp", hp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        if (getArguments() != null) {
            user_id = getArguments().getInt("user_id");
//            nama = getArguments().getString("nama");
//            username = getArguments().getString("username");
//            password = getArguments().getString("password");
//            alamat = getArguments().getString("alamat");
//            hp = getArguments().getString("hp");

            //Log.d("info user", nama + username + password + alamat + hp);
        }

        nama_user = view.findViewById(R.id.nama_user);
        alamat_user = view.findViewById(R.id.alamat_user);
        hp_user = view.findViewById(R.id.hp_user);


        history_button = view.findViewById(R.id.history_button);
        history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), OrderHistory.class);
                i.putExtra("user_id", user_id);
                startActivity(i);
            }
        });

        logout_button = view.findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new logout().execute();
            }
        });

        edit_icon = view.findViewById(R.id.edit_icon);
        edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditAccount.class);
                i.putExtra("user_id", user_id);
                i.putExtra("nama", nama);
                i.putExtra("username", username);
                i.putExtra("password", password);
                i.putExtra("alamat", alamat);
                i.putExtra("hp", hp);

                startActivity(i);
            }
        });

        new getUser().execute();
        return view;
    }

    private class logout extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();

            //login
            String u = "http://192.168.26.97:8000/logout";

            ArrayList<String> mykey = new ArrayList<String>();
            ArrayList myvalue = new ArrayList<>();

            String s = h.postAccess(u, mykey, myvalue);
            Log.d("REST01", "Result cr acc: " + s);

            if (s!=null){
                try {
                    JSONObject jo = new JSONObject(s);

                    int status = jo.getInt("status");
                    if (status == 200) {
                        showPopup("Logout berhasil. Bye bye!");

                    } else if (status == 403){
                        showPopup("Logout gagal");
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
        popupFragment.show(getChildFragmentManager(), "PopupFragment");
    }

    private class getUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();
            String u = "http://192.168.26.97:8000/user/"+user_id;
            String s = h.getAccess(u);

            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    int status = jo.getInt("status");
                    if (status == 200) {
                        JSONArray responseArray = jo.getJSONArray("response");
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject menuObject = responseArray.getJSONObject(j);

                            nama = menuObject.getString("nama");
                            alamat = menuObject.getString("alamat");
                            username = menuObject.getString("username");
                            hp = menuObject.getString("hp");

                        }


                        // Setelah mendapatkan data baru, update UI di thread utama
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nama_user.setText(nama);
                                alamat_user.setText(alamat);
                                hp_user.setText(hp);
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
}
