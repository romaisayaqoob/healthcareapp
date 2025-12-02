package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements DoctorAdapter.OnDoctorClickListener {

    private DoctorAdapter adapter;
    private TextView txtHello, txtTodayAppointments;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);


        LinearLayout tileEmergency, tileHospital, tilePharmacy;

        tileEmergency = v.findViewById(R.id.tileEmergency);
        tileHospital = v.findViewById(R.id.tileHospital);
        tilePharmacy = v.findViewById(R.id.tilePharmacy);

        // -------------------- EMERGENCY CALL --------------------
        tileEmergency.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1122"));
            startActivity(intent);
        });

// -------------------- HOSPITALS NEAR ME --------------------
        tileHospital.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=hospitals near me");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

// -------------------- PHARMACIES NEAR ME --------------------
        tilePharmacy.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=pharmacies near me");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        txtHello = v.findViewById(R.id.txtHello);
        /*txtTodayAppointments = v.findViewById(R.id.txtTodayAppointments);*/
        etSearch = v.findViewById(R.id.etSearchDoctor);

        RecyclerView rv = v.findViewById(R.id.recycler_doctors);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new DoctorAdapter(new ArrayList<>(), this);
        rv.setAdapter(adapter);

        // Load patient name
        SharedPreferences sp = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sp.getString("user_email", null);
        String name = sp.getString("user_name", ""); // assume saved on login
        if (!name.isEmpty()) {
            txtHello.setText("Hello, " + name);
        }

        /*txtTodayAppointments.setText("Today's Appointments: 0"); // placeholder*/

        // Search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { adapter.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Fetch doctors from Realtime DB who completed profile
        // Fetch doctors from Realtime DB who completed profile
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("doctors");

        DatabaseReference usersRef = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("users");

        /*doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Doctor> doctorList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Doctor doctor = ds.getValue(Doctor.class);
                    if (doctor != null) {
                        doctor.setId(ds.getKey());

                        // Fetch the corresponding user for this doctor
                        usersRef.child(doctor.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    String name = userSnapshot.child("name").getValue(String.class);
                                    doctor.setName(name);

                                    // Only add doctors who completed profile
                                    if (doctor.getHospital() != null && !doctor.getHospital().isEmpty()
                                            && doctor.getSpecialization() != null && !doctor.getSpecialization().isEmpty()
                                            && doctor.getExperience() != null
                                            && doctor.getQualification() != null && !doctor.getQualification().isEmpty()) {
                                        doctorList.add(doctor);
                                        adapter.updateList(doctorList);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });*/

        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("HomeFragment", "=== STARTING DOCTOR FETCH ===");
                Log.d("HomeFragment", "Total doctor nodes found: " + snapshot.getChildrenCount());

                List<Doctor> doctorList = new ArrayList<>();
                final int totalDoctors = (int) snapshot.getChildrenCount();
                final int[] processedCount = {0};

                if (totalDoctors == 0) {
                    Log.d("HomeFragment", "No doctors found in database");
                    adapter.updateList(doctorList);
                    return;
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d("HomeFragment", "Processing doctor node: " + ds.getKey());

                    Doctor doctor = ds.getValue(Doctor.class);
                    if (doctor != null) {
                        doctor.setId(ds.getKey());
                        Log.d("HomeFragment", "Doctor object created with ID: " + doctor.getId());
                        Log.d("HomeFragment", "Doctor current data - Hospital: " + doctor.getHospital()
                                + ", Specialization: " + doctor.getSpecialization()
                                + ", Experience: " + doctor.getExperience()
                                + ", Qualification: " + doctor.getQualification());

                        // Fetch the corresponding user for this doctor
                        Log.d("HomeFragment", "Querying users node for doctor ID: " + doctor.getId());
                        usersRef.child(doctor.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                Log.d("HomeFragment", "User query result for " + doctor.getId() + " - exists: " + userSnapshot.exists());

                                if (userSnapshot.exists()) {
                                    String name = userSnapshot.child("name").getValue(String.class);
                                    doctor.setName(name);
                                    Log.d("HomeFragment", "Doctor name set to: " + name);

                                    // Check profile completion
                                    boolean hasHospital = doctor.getHospital() != null && !doctor.getHospital().isEmpty();
                                    boolean hasSpecialization = doctor.getSpecialization() != null && !doctor.getSpecialization().isEmpty();
                                    boolean hasExperience = doctor.getExperience() != null;
                                    boolean hasQualification = doctor.getQualification() != null && !doctor.getQualification().isEmpty();

                                    Log.d("HomeFragment", "Profile completion check for " + name + ":");
                                    Log.d("HomeFragment", "  - Has Hospital: " + hasHospital);
                                    Log.d("HomeFragment", "  - Has Specialization: " + hasSpecialization);
                                    Log.d("HomeFragment", "  - Has Experience: " + hasExperience);
                                    Log.d("HomeFragment", "  - Has Qualification: " + hasQualification);

                                    // Only add doctors who completed profile
                                    if (hasHospital && hasSpecialization && hasExperience && hasQualification) {
                                        synchronized (doctorList) {
                                            doctorList.add(doctor);
                                            Log.d("HomeFragment", "✓ Doctor ADDED to list: " + name + " (Total in list: " + doctorList.size() + ")");
                                        }
                                    } else {
                                        Log.d("HomeFragment", "✗ Doctor NOT added (incomplete profile): " + name);
                                    }
                                } else {
                                    Log.d("HomeFragment", "✗ No user found for doctor ID: " + doctor.getId());
                                }

                                // Increment counter and update adapter when all are processed
                                processedCount[0]++;
                                Log.d("HomeFragment", "Progress: " + processedCount[0] + "/" + totalDoctors + " processed");

                                if (processedCount[0] == totalDoctors) {
                                    Log.d("HomeFragment", "=== ALL DOCTORS PROCESSED ===");
                                    Log.d("HomeFragment", "Final doctor list size: " + doctorList.size());

                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            adapter.updateList(doctorList);
                                            Log.d("HomeFragment", "Adapter updated with " + doctorList.size() + " doctors");
                                            Toast.makeText(getContext(),
                                                    "Loaded " + doctorList.size() + " doctors",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    } else {
                                        Log.e("HomeFragment", "ERROR: Activity is null, cannot update UI");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("HomeFragment", "User query CANCELLED for doctor ID: " + doctor.getId() + " - Error: " + error.getMessage());

                                // Still increment counter even on error
                                processedCount[0]++;
                                Log.d("HomeFragment", "Progress (after error): " + processedCount[0] + "/" + totalDoctors);

                                if (processedCount[0] == totalDoctors) {
                                    Log.d("HomeFragment", "All processed (with errors), updating adapter");
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {
                                            adapter.updateList(doctorList);
                                        });
                                    }
                                }
                            }
                        });
                    } else {
                        Log.e("HomeFragment", "ERROR: Doctor object is NULL for node: " + ds.getKey());
                        // If doctor is null, still increment counter
                        processedCount[0]++;
                        Log.d("HomeFragment", "Progress (null doctor): " + processedCount[0] + "/" + totalDoctors);

                        if (processedCount[0] == totalDoctors) {
                            Log.d("HomeFragment", "All processed, updating adapter");
                            adapter.updateList(doctorList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "DOCTORS QUERY CANCELLED: " + error.getMessage());
                Toast.makeText(getContext(), "Error loading doctors: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
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
