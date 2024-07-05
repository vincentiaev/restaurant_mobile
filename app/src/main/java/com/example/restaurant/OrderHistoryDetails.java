package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class OrderHistoryDetails extends AppCompatActivity {

    String order_id, total_harga, tgl_order, waktu_order, alamat_pengiriman, order_detail, status_pesanan, tanggal;
    TextView order_id_tv, status_tv, waktu_tv, order_detail_tv, alamat_tv, total_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);
        Intent i = getIntent();
        order_id = i.getStringExtra("order_id");
        total_harga = i.getStringExtra("total_harga");
        tgl_order = i.getStringExtra("tgl_order");
        waktu_order = i.getStringExtra("waktu_order");
        alamat_pengiriman = i.getStringExtra("alamat_pengiriman");
        order_detail = i.getStringExtra("order_detail");
        status_pesanan = i.getStringExtra("status_pesanan");
        tanggal = tgl_order + " " + waktu_order;

        Log.d("cobaa", String.valueOf(order_id));
        Log.d("cobaa", String.valueOf(total_harga));
        Log.d("cobaa", tgl_order);
        Log.d("cobaa", waktu_order);
        Log.d("cobaa", alamat_pengiriman);
        Log.d("cobaa", order_detail);
        Log.d("cobaa", status_pesanan);

        order_id_tv = findViewById(R.id.order_id_tv);
        status_tv = findViewById(R.id.status_tv);
        waktu_tv = findViewById(R.id.waktu_tv);
        alamat_tv = findViewById(R.id.alamat_tv);
        order_detail_tv = findViewById(R.id.order_detail_tv);
        total_tv = findViewById(R.id.total_tv);

        order_id_tv.setText(order_id);
        status_tv.setText(status_pesanan);
        waktu_tv.setText(tanggal);
        alamat_tv.setText(alamat_pengiriman);
        order_detail_tv.setText(order_detail);
        total_tv.setText(total_harga);
    }
}