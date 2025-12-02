package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DoctorProfileFragment extends Fragment {

    private EditText edtHospital, edtSpecialization, edtExperience, edtQualification;
    private Button btnSave;
    private DBHelper dbHelper;
    private String doctorUid;

    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        edtHospital = view.findViewById(R.id.edtHospital);
        edtSpecialization = view.findViewById(R.id.edtSpecialization);
        edtExperience = view.findViewById(R.id.edtExperience);
        edtQualification = view.findViewById(R.id.edtQualification);
        btnSave = view.findViewById(R.id.btnSaveProfile);

        dbHelper = new DBHelper();

        SharedPreferences sp = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        doctorUid = sp.getString("user_uid", null);

        if (doctorUid == null) {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Load doctor data
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
                Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.updateDoctorProfile(doctorUid, hospital, specialization, experience, qualification,
                    unused -> Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        });

        return view;
    }
}
