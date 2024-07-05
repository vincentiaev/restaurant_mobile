package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Cart extends AppCompatActivity {
    ArrayList<HashMap<String, String>> cartList;
    ListView cart_lv;
    Integer user_id;
    String alamat;
    FrameLayout menuDetails, darkenBackground, rootLayout;
    LinearLayout total_price_layout;
    TextView total_price_value;

    Button placeOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent i = getIntent();
        user_id = i.getIntExtra("user_id", 0);
        alamat = i.getStringExtra("alamat");

        menuDetails = findViewById(R.id.menu_details);
        darkenBackground = findViewById(R.id.dark_background);
        rootLayout = findViewById(R.id.root_layout);

        cartList = new ArrayList<>();

        total_price_layout = findViewById(R.id.total_price_layout);
        total_price_value = findViewById(R.id.total_price_value);

        cart_lv = findViewById(R.id.cart_lv);
        cart_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> selectedItem = cartList.get(position);

                Bundle bundle = new Bundle();
                bundle.putInt("user_id", user_id);
                Log.d("menufragmentuserid1", String.valueOf(user_id));
                bundle.putString("menu_id", selectedItem.get("menu_id"));
                bundle.putString("nama_menu", selectedItem.get("nama_menu"));
                bundle.putString("deskripsi", selectedItem.get("deskripsi"));
                bundle.putString("harga", selectedItem.get("harga"));
                bundle.putString("gambar", selectedItem.get("gambar"));

                MenuDetailsFragment menuDetailsFragment = new MenuDetailsFragment();
                menuDetailsFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.menu_details, menuDetailsFragment);
                transaction.commit();

                // Show the menu_details_container FrameLayout
                menuDetails.setVisibility(View.VISIBLE);
                darkenBackground.setVisibility(View.VISIBLE);
                total_price_layout.setVisibility(View.GONE);
                cart_lv.setEnabled(false);
                cart_lv.setClickable(false);
            }
        });

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuDetails.getVisibility() == View.VISIBLE) {
                    // Check if the touch event is outside the menuDetails FrameLayout
                    int[] location = new int[2];
                    menuDetails.getLocationOnScreen(location);
                    float x = event.getRawX();
                    float y = event.getRawY();

                    if (x < location[0] || x > location[0] + menuDetails.getWidth() ||
                            y < location[1] || y > location[1] + menuDetails.getHeight()) {
                        // Hide the menuDetails fragment
                        menuDetails.setVisibility(View.GONE);
                        total_price_layout.setVisibility(View.VISIBLE);
                        darkenBackground.setVisibility(View.GONE);
                        cart_lv.setEnabled(true);
                        cart_lv.setClickable(true);
                        return true;
                    }
                }
                return false;
            }
        });

        placeOrderButton = findViewById(R.id.placeOrderButton);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartList.isEmpty()) {
                    showPopup("Keranjang kosong. Tidak bisa memproses pesanan.");
                } else {
                    new placeOrder().execute();
                    new clearCart().execute();
                }
            }
        });


        new getCart().execute();
    }



    private class getCart extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Clear the cartList before loading new items
            cartList.clear();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            CartAdapter adapter = new CartAdapter(Cart.this, cartList);
            cart_lv.setAdapter(adapter);

            int totalBelanjaan = calculateTotalPrice();

            // Tampilkan total belanjaan di TextView total_price_value
            total_price_value.setText("Rp " + totalBelanjaan);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler handler = new HTTPHandler();
            String url = "http://192.168.26.97:8000/cart/" + user_id;
            String response = handler.getAccess(url);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        JSONArray responseArray = jsonObject.getJSONArray("response");
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject orderObject = responseArray.getJSONObject(j);

                            int cart_id = orderObject.getInt("cart_id");
                            int menu_id = orderObject.getInt("menu_id");
                            String nama_menu = fetchMenuName(menu_id);
                            String gambar = fetchGambar(menu_id);
                            String deskripsi = fetchMenuDeskripsi(menu_id);
                            int harga = fetchMenuPrice(menu_id);
                            int jumlah = orderObject.getInt("jumlah");

                            Log.d("cartlist", "cart_id: " + cart_id);
                            Log.d("cartlist", "menu_id: " + menu_id);
                            Log.d("cartlist", "nama_menu: " + nama_menu);
                            Log.d("cartlist", "harga: " + harga);
                            Log.d("cartlist", "jumlah: " + jumlah);
                            Log.d("cartlist", "gambar: " + gambar);
                            Log.d("cartlist", "deskripsi: " + deskripsi);

                            HashMap<String, String> cartMap = new HashMap<>();
                            cartMap.put("cart_id", String.valueOf(cart_id));
                            cartMap.put("menu_id", String.valueOf(menu_id));
                            cartMap.put("nama_menu", nama_menu);
                            cartMap.put("deskripsi", deskripsi);
                            cartMap.put("gambar", gambar);
                            cartMap.put("harga", String.valueOf(harga));
                            cartMap.put("jumlah", String.valueOf(jumlah));

                            cartList.add(cartMap);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    private String fetchMenuName(int menu_id) throws JSONException {
        String menuUrl = "http://192.168.26.97:8000/menu/" + menu_id;
        //Log.d("REST01", menuUrl);
        HTTPHandler handler = new HTTPHandler();
        String menuResponse = handler.getAccess(menuUrl);

        String nama_menu = null;
        if (menuResponse != null) {
            JSONObject menuObject = new JSONObject(menuResponse);
            if (menuObject.getInt("status") == 200) {
                JSONObject responseObject = menuObject.getJSONObject("response");
                nama_menu = responseObject.getString("nama_menu");


            }
        }
        return nama_menu;
    }

    private String fetchMenuDeskripsi(int menu_id) throws JSONException {
        String menuUrl = "http://192.168.26.97:8000/menu/" + menu_id;
        //Log.d("REST01", menuUrl);
        HTTPHandler handler = new HTTPHandler();
        String menuResponse = handler.getAccess(menuUrl);

        String deskripsi = null;
        if (menuResponse != null) {
            JSONObject menuObject = new JSONObject(menuResponse);
            if (menuObject.getInt("status") == 200) {
                JSONObject responseObject = menuObject.getJSONObject("response");
                deskripsi = responseObject.getString("deskripsi");


            }
        }
        return deskripsi;
    }
    private String fetchGambar(int menu_id) throws JSONException {
        String menuUrl = "http://192.168.26.97:8000/menu/" + menu_id;
        //Log.d("REST01", menuUrl);
        HTTPHandler handler = new HTTPHandler();
        String menuResponse = handler.getAccess(menuUrl);

        String gambar = null;
        if (menuResponse != null) {
            JSONObject menuObject = new JSONObject(menuResponse);
            if (menuObject.getInt("status") == 200) {
                JSONObject responseObject = menuObject.getJSONObject("response");
                gambar = responseObject.getString("gambar");


            }
        }
        return gambar;
    }


    private int fetchMenuPrice(int menu_id) throws JSONException {
        String menuUrl = "http://192.168.26.97:8000/menu/" + menu_id;
        //Log.d("REST01", menuUrl);
        HTTPHandler handler = new HTTPHandler();
        String menuResponse = handler.getAccess(menuUrl);

        int harga_menu = 0;
        if (menuResponse != null) {
            JSONObject menuObject = new JSONObject(menuResponse);
            if (menuObject.getInt("status") == 200) {
                JSONObject responseObject = menuObject.getJSONObject("response");
                harga_menu = responseObject.getInt("harga");

            }
        }
        return harga_menu;
    }

    private int calculateTotalPrice() {
        int totalPrice = 0;
        for (HashMap<String, String> item : cartList) {
            int harga = Integer.parseInt(item.get("harga"));
            int jumlah = Integer.parseInt(item.get("jumlah"));
            totalPrice += harga * jumlah;
        }
        return totalPrice;
    }

    public void refreshCart() {
        new getCart().execute();
    }
    private class placeOrder extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler handler = new HTTPHandler();
            String url = "http://192.168.26.97:8000/order";

            // Prepare key and value arrays
            ArrayList<String> mykey = new ArrayList<String>();
            ArrayList myvalue = new ArrayList<>();

            JSONObject orderDetail = new JSONObject();

            try {
                // Build order_detail object
                for (HashMap<String, String> item : cartList) {
                    String menuId = item.get("menu_id");
                    int jumlah = Integer.parseInt(item.get("jumlah"));
                    orderDetail.put(menuId, jumlah);
                }

                mykey.add("order_detail");
                myvalue.add(orderDetail);
                mykey.add("alamat_pengiriman");
                myvalue.add(alamat);
                mykey.add("user_id");
                myvalue.add(user_id);

                // Send POST request to the server
                String response = handler.postAccess(url, mykey, myvalue);
                if (response != null) {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        int status = responseObject.getInt("status");
                        if (status == 200) {
                            showPopup("Berhasil membuat pesanan");
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void showPopup(String message) {
        PopUpFragment popupFragment = PopUpFragment.newInstance(message);
        popupFragment.show(getSupportFragmentManager(), "PopupFragment");
    }

    private class clearCart extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            HTTPHandler h = new HTTPHandler();
            String u = "http://192.168.26.97:8000/cart/del/" + user_id;

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

    }


}