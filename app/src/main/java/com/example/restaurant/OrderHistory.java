package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class OrderHistory extends AppCompatActivity {

    Integer user_id;

    ListView history_lv;
    ArrayList<HashMap<String, String>> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Intent i = getIntent();
        user_id = i.getIntExtra("user_id", 0);
        Log.d("userid", String.valueOf(user_id));

        history_lv = findViewById(R.id.history_lv);
        historyList = new ArrayList<>();

        history_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> order = historyList.get(position);

                Intent i = new Intent(OrderHistory.this, OrderHistoryDetails.class);
                i.putExtra("order_id", order.get("order_id"));
                i.putExtra("tgl_order", order.get("tgl_order"));
                i.putExtra("waktu_order", order.get("waktu_order"));
                i.putExtra("order_detail", order.get("order_detail"));
                i.putExtra("alamat_pengiriman", order.get("alamat_pengiriman"));
                i.putExtra("total_harga", order.get("total_harga"));
                i.putExtra("status_pesanan", order.get("status_pesanan"));
                startActivity(i);
            }
        });



        new OrderHistory.getHistory().execute();
    }

    private class getHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            ListAdapter a = new SimpleAdapter(OrderHistory.this, historyList, R.layout.history_list,
                    new String[]{"tgl_order", "alamat_pengiriman", "total_harga", "waktu_order"},
                    new int[]{R.id.tanggal, R.id.alamat, R.id.total, R.id.waktu});
            history_lv.setAdapter(a);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler handler = new HTTPHandler();
            String url = "http://192.168.26.97:8000/order/" + user_id;
            String response = handler.getAccess(url);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        JSONArray responseArray = jsonObject.getJSONArray("response");
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject orderObject = responseArray.getJSONObject(j);

                            int order_id = orderObject.getInt("order_id");
                            String tgl_order = orderObject.getString("tgl_order");
                            String waktu_order = parseTime(tgl_order);
                            String order_detail = orderObject.getString("order_detail");
                            String formattedOrderDetail = fetchMenuDetails(order_detail);
                            String alamat_pengiriman = orderObject.getString("alamat_pengiriman");
                            int total_harga = orderObject.getInt("total_harga");
                            String status_pesanan = orderObject.getString("status");

                            Log.d("orderhistory", "Order ID: " + order_id);
                            Log.d("orderhistory", "tanggal_order: " + tgl_order);
                            Log.d("orderhistory", "waktu_order: " + waktu_order);
                            Log.d("orderhistory", "order_detail: " + formattedOrderDetail);
                            Log.d("orderhistory", "alamat_pengiriman: " + alamat_pengiriman);
                            Log.d("orderhistory", "total_harga: " + total_harga);
                            Log.d("orderhistory", "status: " + status_pesanan);

                            HashMap<String, String> orderMap = new HashMap<>();
                            orderMap.put("order_id", String.valueOf(order_id));
                            orderMap.put("tgl_order", parseDate(tgl_order));
                            orderMap.put("waktu_order", waktu_order);
                            orderMap.put("order_detail", formattedOrderDetail);
                            orderMap.put("alamat_pengiriman", alamat_pengiriman);
                            orderMap.put("total_harga", "Rp " + String.valueOf(total_harga));
                            orderMap.put("status_pesanan", status_pesanan);
                            historyList.add(orderMap);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

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
}
