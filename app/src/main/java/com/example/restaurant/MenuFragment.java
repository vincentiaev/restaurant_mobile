package com.example.restaurant;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuFragment extends Fragment {
    static int user_id;
    ListView menu_lv;
    ArrayList<HashMap<String, String>> menuList;
    FrameLayout menuDetails, rootLayout, darkenBackground;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(Integer user_id) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        if (getArguments() != null) {
            user_id = getArguments().getInt("user_id");
            Log.d("menufragmentuserid1", String.valueOf(user_id));

        }

        menu_lv = view.findViewById(R.id.menu_lv);
        menuList = new ArrayList<>();

        menuDetails = view.findViewById(R.id.menu_details);

        rootLayout = view.findViewById(R.id.root_layout);
        darkenBackground = view.findViewById(R.id.dark_background);

        new getMenu().execute();

        menu_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> selectedItem = menuList.get(position);

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

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.menu_details, menuDetailsFragment);
                transaction.commit();

                // Show the menu_details_container FrameLayout
                menuDetails.setVisibility(View.VISIBLE);
                darkenBackground.setVisibility(View.VISIBLE);
                menu_lv.setEnabled(false);
                menu_lv.setClickable(false);
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
                        darkenBackground.setVisibility(View.GONE);
                        menu_lv.setEnabled(true);
                        menu_lv.setClickable(true);
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    private class getMenu extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            MenuAdapter adapter = new MenuAdapter(getContext(), menuList);
            menu_lv.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HTTPHandler h = new HTTPHandler();
            String u = "http://192.168.26.97:8000/menu";
            String s = h.getAccess(u);

            if (s != null) {
                try {
                    JSONObject jo = new JSONObject(s);
                    int status = jo.getInt("status");
                    if (status == 200) {
                        JSONArray responseArray = jo.getJSONArray("response");
                        for (int j = 0; j < responseArray.length(); j++) {
                            JSONObject menuObject = responseArray.getJSONObject(j);

                            int menu_id = menuObject.getInt("menu_id");
                            String nama_menu = menuObject.getString("nama_menu");
                            String deskripsi = menuObject.getString("deskripsi");
                            int harga = menuObject.getInt("harga");
                            String gambar = menuObject.getString("gambar");

                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("menu_id", Integer.toString(menu_id));
                            hm.put("nama_menu", nama_menu);
                            hm.put("deskripsi", deskripsi);
                            hm.put("harga", Integer.toString(harga));
                            hm.put("gambar", gambar);
                            menuList.add(hm);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }
}
