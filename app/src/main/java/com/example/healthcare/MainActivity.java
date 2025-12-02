package com.example.healthcare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Make sure your layout has:
        // FrameLayout with id 'fragment_container'
        // BottomNavigationView with id 'bottom_nav'

        // Initialize Realtime DB
        realtimeDB = FirebaseDatabase.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) selected = new HomeFragment();

            else if (id == R.id.nav_schedule) selected = new ScheduleFragment(); // create later
            else if (id == R.id.nav_messages) selected = new MessagesFragment(); // placeholder
            else if (id == R.id.nav_settings) selected = new SettingsFragment(); // placeholder


            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        // default selected item
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}
