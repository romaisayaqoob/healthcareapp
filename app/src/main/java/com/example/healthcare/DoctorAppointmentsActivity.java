package com.example.healthcare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rvAppointments;
    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private DatabaseReference appointmentsRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

        rvAppointments = findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DoctorAppointmentAdapter(appointmentList, new DoctorAppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onAccept(Appointment appointment) {
                updateAppointmentStatus(appointment, "APPROVED");
            }

            @Override
            public void onComplete(Appointment appointment) {
                updateAppointmentStatus(appointment, "COMPLETED");
            }

            @Override
            public void onCancel(Appointment appointment) {
                updateAppointmentStatus(appointment, "CANCELLED");
            }
        });
        rvAppointments.setAdapter(adapter);

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String doctorId = sp.getString("user_uid", null); // doctor ID saved on login
        if (doctorId == null) return;

        appointmentsRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("appointments");

        usersRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        loadAppointments(doctorId);
    }

    private void loadAppointments(String doctorId) {
        appointmentsRef.get().addOnSuccessListener(snapshot -> {
            appointmentList.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {
                Appointment appt = ds.getValue(Appointment.class);
                if (appt != null && appt.getDoctorId().equals(doctorId)) {
                    appt.setId(ds.getKey());  // <-- save Firebase key
                    appointmentList.add(appt);

                    // Fetch patient name asynchronously
                    String patientId = appt.getPatientId();
                    if (patientId != null && !patientId.isEmpty()) {
                        usersRef.child(patientId).get().addOnSuccessListener(userSnap -> {
                            if (userSnap.exists()) {
                                String patientName = userSnap.child("name").getValue(String.class);
                                appt.setPatientId(patientName != null ? patientName : patientId);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }

            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show());
    }

    private void updateAppointmentStatus(Appointment appt, String status) {
        appt.setStatus(status);

        // You should store appointments under a unique appointmentId in Firebase
        // Here assuming appt has a unique id field called 'id'
        if (appt.getId() == null) return;  // make sure ID exists

        appointmentsRef.child(appt.getId()).setValue(appt)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Status updated to " + status, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());

    }
}
