package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsFragment extends Fragment {

    private RecyclerView rvAppointments;
    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private DatabaseReference appointmentsRef;
    private DatabaseReference usersRef;
    private String doctorId;

    public DoctorAppointmentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_doctor_appointments, container, false);

        rvAppointments = view.findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView txtWelcome = view.findViewById(R.id.txtWelcome);
        // Load doctor name


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

        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        doctorId = sp.getString("user_uid", null); // doctor ID saved on login
        if (doctorId == null) return view;

        appointmentsRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("appointments");

        usersRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        loadAppointments(doctorId);
        usersRef.child(doctorId).get().addOnSuccessListener(snap -> {
            if (snap.exists()) {
                String name = snap.child("name").getValue(String.class);
                if (name != null) {
                    String formatted = "Welcome Back Dr. " + capitalizeWords(name);
                    txtWelcome.setText(formatted);
                }
            }
        });

        return view;
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
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show());
    }

    private void updateAppointmentStatus(Appointment appt, String status) {
        appt.setStatus(status);

        if (appt.getId() == null) return;  // make sure ID exists

        appointmentsRef.child(appt.getId()).setValue(appt)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Status updated to " + status, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update status", Toast.LENGTH_SHORT).show());
    }
    private String capitalizeWords(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            sb.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

}
