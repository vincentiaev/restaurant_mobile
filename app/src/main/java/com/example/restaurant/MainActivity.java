package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    String nama, password, username, alamat, hp;
    Integer user_id;

    BottomNavigationView bottomNavigationView;
    MenuFragment menuFragment;
    OrderFragment orderFragment;
    UserFragment userFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.cart) {
            // Aksi ketika ikon cart diklik
            Intent i = new Intent(this, Cart.class);
            i.putExtra("user_id", user_id);
            i.putExtra("alamat", alamat);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Intent i = getIntent();
        user_id = i.getIntExtra("user_id", 0);
        nama = i.getStringExtra("nama");
        username = i.getStringExtra("username");
        password = i.getStringExtra("password");
        alamat = i.getStringExtra("alamat");
        hp = i.getStringExtra("hp");
        Log.d("info user", user_id + nama + username + password + alamat + hp);

        // Initialize fragments with user_id and other data
        menuFragment = MenuFragment.newInstance(user_id);
        orderFragment = OrderFragment.newInstance(user_id, alamat);
        userFragment = UserFragment.newInstance(user_id, nama, username, password, alamat, hp);

        // Load the default fragment (MenuFragment) when activity is created
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, menuFragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment selectedFragment = null;

        if (itemId == R.id.menu) {
            selectedFragment = menuFragment;
        } else if (itemId == R.id.order) {
            selectedFragment = orderFragment;
        } else if (itemId == R.id.user) {
            selectedFragment = userFragment;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, selectedFragment)
                    .commit();
            return true;
        }

        return false;
    }
}
