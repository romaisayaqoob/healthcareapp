package com.example.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorMainActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnAppointments, btnProfile, btnLogout;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnAppointments = findViewById(R.id.btnAppointments);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DBHelper();

        // Load doctor name from SharedPreferences
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String email = sp.getString("user_email", "");
        dbHelper.getNameByEmail(email, name -> tvWelcome.setText("Welcome, Dr. " + name));

        btnAppointments.setOnClickListener(v -> startActivity(new Intent(this, DoctorAppointmentsActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, DoctorProfileActivity.class)));
        btnLogout.setOnClickListener(v -> {
            sp.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
