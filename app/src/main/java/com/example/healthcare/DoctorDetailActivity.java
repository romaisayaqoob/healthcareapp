package com.example.healthcare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class DoctorDetailActivity extends AppCompatActivity {
    private DBHelper db;
    private String doctorId;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        db = new DBHelper();
        doctorId = getIntent().getStringExtra("doctor_id"); // Firestore IDs are Strings
        if (doctorId == null) {
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.detail_name);
        TextView tvSpec = findViewById(R.id.detail_spec);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        // Load doctors asynchronously
        db.getAllDoctors(new OnSuccessListener<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> doctors) {
                for (Doctor d : doctors) {
                    if (d.getId().equals(doctorId)) {
                        doctor = d;
                        tvName.setText(doctor.getName());
                        tvSpec.setText(doctor.getSpecialization() + " • " + doctor.getHospital());
                        break;
                    }
                }
                if (doctor == null) {
                    Toast.makeText(DoctorDetailActivity.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (doctor == null) return;

            String date = "2025-11-20"; // TODO: replace with actual picker
            String time = "09:00 AM";

            Appointment ap = new Appointment(doctorId, date, time, "Upcoming");

            db.createAppointment(ap,
                    docRef -> {
                        Toast.makeText(this, "Appointment created", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    e -> Toast.makeText(this, "Failed to create appointment", Toast.LENGTH_SHORT).show()
            );

        });
    }
}
