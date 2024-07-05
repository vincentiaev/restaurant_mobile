package com.example.restaurant;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class OrderFragment extends Fragment {

    public OrderFragment() {
        // Required empty public constructor
    }
    TextView order_id_tv, status_tv, waktu_tv, order_detail_tv, alamat_tv, total_tv, estimated_time_tv;
    ImageView gambar;
    static int user_id;
    String alamat;
    LinearLayout ongoing_order;
    FrameLayout gambar_kosong;

    public static OrderFragment newInstance(Integer user_id, String alamat) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", user_id);
        args.putString("alamat", alamat);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            user_id = getArguments().getInt("user_id");
            alamat = getArguments().getString("alamat");

            Log.d("orderfragmentcoba", user_id + alamat);
        }

        new OrderFragment.getOngoingOrder().execute();

        gambar = view.findViewById(R.id.gambar);

        order_id_tv = view.findViewById(R.id.order_id_tv);
        status_tv = view.findViewById(R.id.status_tv);
        waktu_tv = view.findViewById(R.id.waktu_tv);
        alamat_tv = view.findViewById(R.id.alamat_tv);
        order_detail_tv = view.findViewById(R.id.order_detail_tv);
        total_tv = view.findViewById(R.id.total_tv);
        estimated_time_tv = view.findViewById(R.id.estimated_time);

        ongoing_order = view.findViewById(R.id.ongoing_order);
        gambar_kosong = view.findViewById(R.id.gambar_kosong);

        return view;
    }

    private class getOngoingOrder extends AsyncTask<Void, Void, Void> {
        //method yg nunjukkin klo proses bacanya udh slese baru lakuin ini
        //listviewnya
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String currentDateAndTime = sdf.format(new Date());

            String u = "http://192.168.26.97:8000/ongoing/" + String.valueOf(currentDateAndTime) + "/" + user_id;
            String s = h.getAccess(u);
            //Log.d("REST01", u+"Result get: " + s);

            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);

                    int status = jo.getInt("status");
                    if (status == 200) {
                        JSONArray responseArray = jo.getJSONArray("response");
                        Log.d("ongoingorder", "resp: " + responseArray);
                        if (responseArray != null){
                            if (responseArray.length() == 0){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        ongoing_order.setVisibility(View.GONE);
                                        gambar_kosong.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        ongoing_order.setVisibility(View.VISIBLE);
                                        gambar_kosong.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }


                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject orderObject = responseArray.getJSONObject(j);

                            int order_id = orderObject.getInt("order_id");
                            String tgl_order = orderObject.getString("tgl_order");
                            String tanggal  = parseDate(tgl_order);
                            String waktu_order = parseTime(tgl_order);
                            String order_detail = orderObject.getString("order_detail");
                            String formattedOrderDetail = fetchMenuDetails(order_detail);
                            String alamat_pengiriman = orderObject.getString("alamat_pengiriman");

                            String estimated_time = addOneHour(waktu_order);

                            int total_harga = orderObject.getInt("total_harga");
                            String status_pesanan = orderObject.getString("status");

                            Log.d("ongoingorder", "Order ID: " + order_id);
                            Log.d("ongoingorder", "tanggal_order: " + tgl_order);
                            Log.d("ongoingorder", "waktu_order: " + waktu_order);
                            Log.d("ongoingorder", "estimated_time: " + estimated_time);
                            Log.d("ongoingorder", "order_detail: " + formattedOrderDetail);
                            Log.d("ongoingorder", "alamat_pengiriman: " + alamat_pengiriman);
                            Log.d("ongoingorder", "total_harga: " + total_harga);
                            Log.d("ongoingorder", "status: " + status_pesanan);


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    order_id_tv.setText(String.valueOf(order_id));
                                    status_tv.setText(status_pesanan);
                                    waktu_tv.setText(tanggal + " " + waktu_order );
                                    alamat_tv.setText(alamat_pengiriman);
                                    order_detail_tv.setText(formattedOrderDetail);
                                    total_tv.setText("Rp " + String.valueOf(total_harga));
                                    estimated_time_tv.setText(estimated_time);

                                    if (status_pesanan.equals("Menunggu konfirmasi")){
                                        gambar.setImageResource(R.mipmap.ic_wait_foreground);
                                    } else if (status_pesanan.equals("Sedang dimasak")) {
                                        gambar.setImageResource(R.mipmap.ic_cook_foreground);
                                    } else if (status_pesanan.equals("Dalam perjalanan")) {
                                        gambar.setImageResource(R.mipmap.ic_deliver_foreground);
                                    }
                                }
                            });



                        }
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            return null;
        }
    }

    private String fetchMenuDetails(String order_detail) {
        StringBuilder formattedOrderDetail = new StringBuilder();
        try {
            JSONObject orderDetailJson = new JSONObject(order_detail);
            Iterator<String> keys = orderDetailJson.keys();
            while (keys.hasNext()) {
                String menu_id = keys.next();
                int quantity = orderDetailJson.getInt(menu_id);

                // Ambil data menu dari API
                String menuUrl = "http://192.168.26.97:8000/menu/" + menu_id;
                //Log.d("REST01", menuUrl);
                HTTPHandler handler = new HTTPHandler();
                String menuResponse = handler.getAccess(menuUrl);

                if (menuResponse != null) {
                    JSONObject menuObject = new JSONObject(menuResponse);
                    if (menuObject.getInt("status") == 200) {
                        JSONObject responseObject = menuObject.getJSONObject("response");
                        String nama_menu = responseObject.getString("nama_menu");
                        int harga_menu = responseObject.getInt("harga");

                        String orderDetailText = "Menu: " + nama_menu + ", Qty: " + quantity + ", Harga: Rp " + harga_menu;
                        formattedOrderDetail.append(orderDetailText).append("\n");
                        Log.d("REST01", orderDetailText);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formattedOrderDetail.toString().trim();
    }

    private String parseDate(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        outputDateFormat.setTimeZone(TimeZone.getDefault()); // Set to device's timezone

        try {
            Date date = inputFormat.parse(dateTime);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String parseTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        outputTimeFormat.setTimeZone(TimeZone.getDefault()); // Set to device's timezone

        try {
            Date date = inputFormat.parse(dateTime);
            return outputTimeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String addOneHour(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}