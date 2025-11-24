package com.example.healthcare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    List<Appointment> appointmentList;

    TextView tabAll, tabUpcoming, tabCompleted, tabCancelled;
    EditText searchBar;

    String currentFilter = "ALL";

    private DBHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.recyclerSchedule);
        searchBar = view.findViewById(R.id.searchAppointments);

        tabAll = view.findViewById(R.id.tabAll);
        tabUpcoming = view.findViewById(R.id.tabUpcoming);
        tabCompleted = view.findViewById(R.id.tabCompleted);
        tabCancelled = view.findViewById(R.id.tabCancelled);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentList = new ArrayList<>();
        adapter = new ScheduleAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(adapter);

        db = new DBHelper();

        loadAppointments(); // load real data from Firestore

        tabAll.setOnClickListener(v -> filter("ALL"));
        tabUpcoming.setOnClickListener(v -> filter("UPCOMING"));
        tabCompleted.setOnClickListener(v -> filter("COMPLETED"));
        tabCancelled.setOnClickListener(v -> filter("CANCELLED"));

        return view;
    }

    private void loadAppointments() {
        // Inside your ScheduleFragment (or wherever you load appointments)
        db.getAllAppointments(new OnSuccessListener<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> appointments) {
                // For each appointment, fetch the corresponding doctor
                for (Appointment a : appointments) {
                    db.getAllDoctors(new OnSuccessListener<List<Doctor>>() {
                        @Override
                        public void onSuccess(List<Doctor> doctors) {
                            for (Doctor d : doctors) {
                                if (d.getId().equals(a.getDoctorId())) {
                                    a.setDoctorName(d.getName());
                                    a.setSpecialty(d.getSpecialization());
                                    a.setHospital(d.getHospital());
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged(); // refresh after doctor info is filled
                        }
                    });
                }

                appointmentList.clear();
                appointmentList.addAll(appointments);
                adapter.notifyDataSetChanged();
            }
        });

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
    }
}
