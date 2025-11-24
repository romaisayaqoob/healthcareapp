package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements DoctorAdapter.OnDoctorClickListener {

    private DBHelper dbHelper;
    private DoctorAdapter adapter;
    private TextView txtHello, txtTodayAppointments;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        txtHello = v.findViewById(R.id.txtHello);
        txtTodayAppointments = v.findViewById(R.id.txtTodayAppointments);
        etSearch = v.findViewById(R.id.etSearchDoctor);

        dbHelper = new DBHelper();
        RecyclerView rv = v.findViewById(R.id.recycler_doctors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);

        // Initialize adapter first
        adapter = new DoctorAdapter(new ArrayList<>(), this);
        rv.setAdapter(adapter);

        // Add dummy doctors
        List<Doctor> dummyDoctors = new ArrayList<>();
        dummyDoctors.add(new Doctor("1", "Dr. John Doe", "Cardiologist", "City Hospital", 10, 500, 4.5));
        dummyDoctors.add(new Doctor("2", "Dr. Jane Smith", "Neurologist", "Metro Hospital", 8, 300, 4.2));
        adapter.updateList(dummyDoctors);

        // Load user name asynchronously
        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sp.getString("user_email", null);
        if (email != null) {
            dbHelper.getNameByEmail(email, name -> txtHello.setText("Hello, " + name));
        }

        txtTodayAppointments.setText("Today's Appointments: 0"); // placeholder

        // Search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { adapter.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });

        return v;
    }

    @Override
    public void onDoctorClick(Doctor doctor) {
        Intent i = new Intent(getContext(), DoctorDetailActivity.class);
        i.putExtra("doctor_id", doctor.getId());
        startActivity(i);
    }
}
