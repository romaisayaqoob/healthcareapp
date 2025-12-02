package com.example.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorMainActivity extends AppCompatActivity {

    TextView tvWelcome;
    DBHelper dbHelper;
    private String doctorId;
    FirebaseDatabase realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);


        dbHelper = new DBHelper();
        realtimeDB = FirebaseDatabase.getInstance();

        // Get doctor ID from SharedPreferences
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        doctorId = sp.getString("user_uid", null);

        /*if (doctorId != null) {
            dbHelper.getDoctorByUid(doctorId, doctor -> {
                if (doctor != null) {
                    tvWelcome.setText("Welcome, Dr. " + doctor.getName());
                }
            });
        }*/

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_doctor);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId(); // <-- avoids constant expression issue
            if (id == R.id.nav_appointments) selectedFragment = new DoctorAppointmentsFragment();
            else if (id == R.id.nav_messages) selectedFragment = new DoctorMessageFragment();
            else if (id == R.id.nav_profile) selectedFragment = new DoctorProfileFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }
            return true;
        });

        // default selected item
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_appointments);
        }
    }
}
