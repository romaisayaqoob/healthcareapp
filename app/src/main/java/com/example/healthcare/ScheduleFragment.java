package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    List<Appointment> appointmentList = new ArrayList<>();

    TextView tabAll, tabUpcoming, tabCompleted, tabCancelled;
    EditText searchBar;

    DatabaseReference appointmentsRef, doctorsRef;
    String patientId;

    String currentFilter = "ALL";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.recyclerSchedule);
        searchBar = view.findViewById(R.id.searchAppointments);

        tabAll = view.findViewById(R.id.tabAll);
        tabUpcoming = view.findViewById(R.id.tabUpcoming);
        tabCompleted = view.findViewById(R.id.tabCompleted);
        tabCancelled = view.findViewById(R.id.tabCancelled);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(adapter);

        SharedPreferences sp = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        patientId = sp.getString("user_uid", null);

        if (patientId == null) {
            Toast.makeText(getContext(), "Login error: patient ID missing", Toast.LENGTH_SHORT).show();
            return view;
        }

        appointmentsRef = FirebaseDatabase
                .getInstance("https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("appointments");

        doctorsRef = FirebaseDatabase
                .getInstance("https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("doctors");

        loadAppointments();

        tabAll.setOnClickListener(v -> filter("ALL"));
        tabUpcoming.setOnClickListener(v -> filter("UPCOMING"));
        tabCompleted.setOnClickListener(v -> filter("COMPLETED"));
        tabCancelled.setOnClickListener(v -> filter("CANCELLED"));

        return view;
    }

    private void loadAppointments() {
        // Get current patient name from shared prefs
        SharedPreferences sp = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String currentPatientName = sp.getString("user_name", null); // <-- store the name on login

        if (currentPatientName == null) {
            Toast.makeText(getContext(), "Patient name not found", Toast.LENGTH_SHORT).show();
            return;
        }

        appointmentsRef.get().addOnSuccessListener(snapshot -> {
            appointmentList.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {
                Appointment appt = ds.getValue(Appointment.class);

                if (appt != null && appt.getPatientId().equals(currentPatientName)) { // match by name for now
                    appt.setId(ds.getKey());
                    appointmentList.add(appt);

                    String dId = appt.getDoctorId();

                    // fetch doctor NAME from users
                    FirebaseDatabase.getInstance("https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference("users").child(dId)
                            .get().addOnSuccessListener(userSnap -> {
                                if (userSnap.exists()) {
                                    appt.setDoctorName(userSnap.child("name").getValue(String.class));
                                }

                                // fetch doctor hospital, specialization from doctors table
                                doctorsRef.child(dId).get().addOnSuccessListener(docSnap -> {
                                    if (docSnap.exists()) {
                                        appt.setSpecialty(docSnap.child("specialization").getValue(String.class));
                                        appt.setHospital(docSnap.child("hospital").getValue(String.class));
                                    }

                                    adapter.notifyDataSetChanged();
                                });
                            });
                }
            }

            adapter.notifyDataSetChanged();

        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show());
    }



    private void filter(String status) {
        currentFilter = status;
        List<Appointment> filtered = new ArrayList<>();

        for (Appointment a : appointmentList) {
            if (status.equals("ALL") || a.getStatus().equalsIgnoreCase(status)) {
                filtered.add(a);
            }
        }

        adapter.updateList(filtered);
        updateTabColors();

    }
    private void updateTabColors() {
        // Reset all tabs to unselected
        int selectedColor = getResources().getColor(android.R.color.white);
        int unselectedColor = getResources().getColor(android.R.color.darker_gray);

        tabAll.setTextColor(unselectedColor);
        tabUpcoming.setTextColor(unselectedColor);
        tabCompleted.setTextColor(unselectedColor);
        tabCancelled.setTextColor(unselectedColor);

        tabAll.setBackgroundResource(R.drawable.tab_unselected_bg);
        tabUpcoming.setBackgroundResource(R.drawable.tab_unselected_bg);
        tabCompleted.setBackgroundResource(R.drawable.tab_unselected_bg);
        tabCancelled.setBackgroundResource(R.drawable.tab_unselected_bg);

        // Highlight selected tab
        switch (currentFilter) {
            case "ALL":
                tabAll.setTextColor(selectedColor);
                tabAll.setBackgroundResource(R.drawable.tab_selected_bg);
                break;
            case "UPCOMING":
                tabUpcoming.setTextColor(selectedColor);
                tabUpcoming.setBackgroundResource(R.drawable.tab_selected_bg);
                break;
            case "COMPLETED":
                tabCompleted.setTextColor(selectedColor);
                tabCompleted.setBackgroundResource(R.drawable.tab_selected_bg);
                break;
            case "CANCELLED":
                tabCancelled.setTextColor(selectedColor);
                tabCancelled.setBackgroundResource(R.drawable.tab_selected_bg);
                break;
        }
    }
}
