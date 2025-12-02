package com.example.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class DoctorProfileActivity extends AppCompatActivity {

    private EditText edtHospital, edtSpecialization, edtExperience, edtQualification;
    private Button btnSave;
    private DBHelper dbHelper;
    private String doctorUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);




        edtHospital = findViewById(R.id.edtHospital);
        edtSpecialization = findViewById(R.id.edtSpecialization);
        edtExperience = findViewById(R.id.edtExperience);
        edtQualification = findViewById(R.id.edtQualification);
        btnSave = findViewById(R.id.btnSaveProfile);

        dbHelper = new DBHelper();

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        doctorUid = sp.getString("user_uid", null); // Make sure UID is saved at login

        if (doctorUid == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing doctor data
        dbHelper.getDoctorByUid(doctorUid, doctor -> {
            if (doctor != null) {
                edtHospital.setText(doctor.getHospital());
                edtSpecialization.setText(doctor.getSpecialization());
                edtExperience.setText(doctor.getExperience());
                edtQualification.setText(doctor.getQualification());
            }
        });

        btnSave.setOnClickListener(v -> {
            String hospital = edtHospital.getText().toString().trim();
            String specialization = edtSpecialization.getText().toString().trim();
            String experience = edtExperience.getText().toString().trim();
            String qualification = edtQualification.getText().toString().trim();

            if (hospital.isEmpty() || specialization.isEmpty() || experience.isEmpty() || qualification.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.updateDoctorProfile(doctorUid, hospital, specialization, experience, qualification,
                    unused -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        });
    }
}
