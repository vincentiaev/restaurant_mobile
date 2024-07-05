package com.example.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> menuList;
    private LayoutInflater inflater;

    public MenuAdapter(Context context, ArrayList<HashMap<String, String>> menuList) {
        this.context = context;
        this.menuList = menuList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_list, parent, false);
        }

        TextView namaMenu = convertView.findViewById(R.id.nama_menu);
        TextView deskripsi = convertView.findViewById(R.id.deskripsi);
        TextView harga = convertView.findViewById(R.id.harga);
        ImageView imageMenu = convertView.findViewById(R.id.image_menu);

        HashMap<String, String> menuItem = menuList.get(position);

        namaMenu.setText(menuItem.get("nama_menu"));
        deskripsi.setText(menuItem.get("deskripsi"));
        harga.setText("Rp "+menuItem.get("harga"));

        String imageUrl = "http://192.168.26.97:8000/" + menuItem.get("gambar");
        Glide.with(context)
                .load(imageUrl)
                .fitCenter() // or .centerCrop() depending on the desired behavior
                .into(imageMenu);

        return convertView;
    }
}
