package com.example.restaurant;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> cartList;
    private LayoutInflater inflater;

    public CartAdapter(Context context, ArrayList<HashMap<String, String>> cartList) {
        this.context = context;
        this.cartList = cartList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cart_list, parent, false);
        }

        TextView namaMenu = convertView.findViewById(R.id.nama_menu);
        TextView harga = convertView.findViewById(R.id.harga);
        TextView jumlah = convertView.findViewById(R.id.jumlah);
        ImageView imageMenu = convertView.findViewById(R.id.image_menu);
        ImageView minus = convertView.findViewById(R.id.minus);


        HashMap<String, String> menuItem = cartList.get(position);

        namaMenu.setText(menuItem.get("nama_menu"));
        harga.setText(menuItem.get("harga"));
        jumlah.setText(menuItem.get("jumlah"));

        String imageUrl = "http://192.168.26.97:8000/" + menuItem.get("gambar");

        Glide.with(context)
                .load(imageUrl)
                .fitCenter() // or .centerCrop() depending on the desired behavior
                .into(imageMenu);


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("cobapls", menuItem.get("cart_id"));
                int jumlah_min = Integer.parseInt((String) jumlah.getText());
                jumlah_min = jumlah_min - 1;
                jumlah.setText(String.valueOf(jumlah_min));

                if (jumlah_min > 0) {
                    // Update jumlah menggunakan AddCartTask
                    new AddCartTask().execute(String.valueOf(jumlah_min), menuItem.get("cart_id"));
                } else {
                    // Hapus item menggunakan DeleteCartTask
                    new DeleteCartTask().execute(menuItem.get("cart_id"));
                }

            }
        });

        return convertView;
    }

    private class AddCartTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            int jumlah = Integer.parseInt(params[0]);
            String cart_id = params[1];

            HTTPHandler h = new HTTPHandler();
            String u = "http://192.168.26.97:8000/cart/"+cart_id;

            ArrayList<String> mykey = new ArrayList<String>();
            ArrayList<String> myvalue = new ArrayList<String>();

            mykey.add("cart_id");
            myvalue.add(cart_id);
            mykey.add("jumlah");
            myvalue.add(String.valueOf(jumlah));

            String s = h.putAccess(u, mykey, myvalue);
            Log.d("REST01", "Result cr acc: " + s);

            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    int status = jo.getInt("status");
                    if (status == 200) {
                        // Berhasil menambahkan ke keranjang
                    } else if (status == 403) {
                        // Forbidden, mungkin akses ditolak
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((Activity) context).recreate();
        }
    }

    private class DeleteCartTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String cart_id = params[0];

            HTTPHandler h = new HTTPHandler();
            String u = "http://192.168.26.97:8000/cart/" + cart_id;

            String s = h.deleteAccess(u);
            Log.d("REST01", "Result delete cart: " + s);

            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    int status = jo.getInt("status");
                    if (status == 200) {
                        // Berhasil menghapus dari keranjang
                        // Perlu memperbarui UI atau data lokal jika diperlukan
                    } else if (status == 403) {
                        // Forbidden, mungkin akses ditolak
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((Activity) context).recreate();
        }
    }

}
