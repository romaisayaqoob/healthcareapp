package com.example.healthcare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorDetailActivity extends AppCompatActivity {

    private TextView tvName, tvSpec, tvExp;
    private Button btnMakeAppointment;
    private String doctorId;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        tvName = findViewById(R.id.detail_name);
        tvSpec = findViewById(R.id.detail_spec);
        tvExp = findViewById(R.id.detail_exp);
        btnMakeAppointment = findViewById(R.id.btn_make_appointment);

        doctorId = getIntent().getStringExtra("doctor_id");
        if (doctorId == null) {
            Toast.makeText(this, "No doctor selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchDoctorDetails(doctorId);

        btnMakeAppointment.setOnClickListener(v -> {
            if (doctor == null) return;

            // 1️⃣ Pick a date
            DatePickerDialog datePicker = new DatePickerDialog(
                    DoctorDetailActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                        // 2️⃣ Pick a time after date selected
                        TimePickerDialog timePicker = new TimePickerDialog(
                                DoctorDetailActivity.this,
                                (timeView, hourOfDay, minute) -> {
                                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute);

                                    // 3️⃣ Save appointment to Firebase
                                    createAppointment(selectedDate, selectedTime);

                                },
                                9, 0, false); // default 9:00 AM
                        timePicker.show();

                    },
                    2025, 11, 30 // default date today
            );
            datePicker.show();
        });

    }
    private void createAppointment(String date, String time) {
        String userId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("user_uid", "unknown"); // patient id

        // Use the new constructor that includes patientId
        Appointment appointment = new Appointment(doctor.getId(), userId, date, time, "REQUESTED");
        appointment.setDoctorName(doctor.getName());
        appointment.setSpecialty(doctor.getSpecialization());
        appointment.setHospital(doctor.getHospital());

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("appointments");

        String apptId = appointmentsRef.push().getKey();
        if (apptId == null) return;

        appointmentsRef.child(apptId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment requested", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to request appointment", Toast.LENGTH_SHORT).show();
                });
    }


    private void fetchDoctorDetails(String doctorId) {
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance("https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("doctors").child(doctorId);
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(doctorId);

        doctorsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                doctor = snapshot.getValue(Doctor.class);
                if (doctor != null) {
                    doctor.setId(snapshot.getKey());

                    // Fetch doctor name from users node
                    usersRef.get().addOnSuccessListener(userSnap -> {
                        if (userSnap.exists()) {
                            String name = userSnap.child("name").getValue(String.class);
                            doctor.setName(name);

                            // Update UI
                            tvName.setText(doctor.getName());
                            tvSpec.setText(doctor.getSpecialization() + " • " + doctor.getHospital());
                            tvExp.setText(doctor.getExperience() + " years experience, " + doctor.getQualification());
                        }
                    }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load doctor name", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Doctor not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load doctor details", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
