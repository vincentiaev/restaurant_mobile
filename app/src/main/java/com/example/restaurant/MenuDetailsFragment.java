package com.example.restaurant;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
public class MenuDetailsFragment extends Fragment {

    String harga, nama_menu, deskripsi, gambar, menu_id;
    int user_id;
    TextView nama_menu_tv, deskripsi_tv, harga_tv;
    ImageView gambar_view;
    Button add_order;


    public MenuDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_details, container, false);

        nama_menu_tv = view.findViewById(R.id.nama_menu);
        deskripsi_tv = view.findViewById(R.id.deskripsi);
        harga_tv = view.findViewById(R.id.harga);
        gambar_view = view.findViewById(R.id.gambar);
        add_order = view.findViewById(R.id.add_order);

        if (getArguments() != null) {
            user_id = getArguments().getInt("user_id");
            menu_id = getArguments().getString("menu_id");
            nama_menu = getArguments().getString("nama_menu");
            deskripsi = getArguments().getString("deskripsi");
            harga = getArguments().getString("harga");
            gambar = getArguments().getString("gambar");
            Log.d("menufragmentuseriduser", String.valueOf(user_id));
        }

        nama_menu_tv.setText(nama_menu);
        harga_tv.setText("Rp " + harga);
        deskripsi_tv.setText(deskripsi);

        String imageUrl = "http://192.168.26.97:8000/" + gambar;
        Glide.with(requireContext())
                .load(imageUrl)
                .into(gambar_view);

        add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Pesanan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                new addCart().execute();
            }
        });

        return view;
    }
    private class addCart extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();

            //login
            String u = "http://192.168.26.97:8000/cart";

            ArrayList<String> mykey = new ArrayList<String>();
            ArrayList myvalue = new ArrayList<>();

            mykey.add("menu_id");
            myvalue.add(menu_id);
            mykey.add("jumlah");
            myvalue.add(1);
            mykey.add("user_id");
            myvalue.add(user_id);

            String s = h.postAccess(u, mykey, myvalue);
            Log.d("REST01", "Result cr acc: " + s);

            if (s!=null){
                try {
                    JSONObject jo = new JSONObject(s);

                    int status = jo.getInt("status");
                    if (status == 200) {
                        FragmentActivity activity = getActivity();
                        if (activity instanceof Cart) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((Cart) activity).refreshCart();
                                }
                            });
                        }
                    } else if (status == 403){

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }
    }

}
