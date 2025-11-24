package com.example.healthcare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorProfileActivity extends AppCompatActivity {

    EditText edtName, edtHospital, edtSpecialization;
    Button btnSave;
    DBHelper dbHelper;
    String doctorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        edtName = findViewById(R.id.edtName);
        edtHospital = findViewById(R.id.edtHospital);
        edtSpecialization = findViewById(R.id.edtSpecialization);
        btnSave = findViewById(R.id.btnSaveProfile);

        dbHelper = new DBHelper();
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        doctorEmail = sp.getString("user_email", null);

        if (doctorEmail == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing info
        dbHelper.getDoctorByEmail(doctorEmail, doctor -> {
            if (doctor != null) {
                edtName.setText(doctor.getName());
                edtHospital.setText(doctor.getHospital());
                edtSpecialization.setText(doctor.getSpecialization());
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String hospital = edtHospital.getText().toString().trim();
            String specialization = edtSpecialization.getText().toString().trim();

            if (name.isEmpty() || hospital.isEmpty() || specialization.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.updateDoctorProfile(doctorEmail, name, hospital, specialization,
                    unused -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        });
    }
}
