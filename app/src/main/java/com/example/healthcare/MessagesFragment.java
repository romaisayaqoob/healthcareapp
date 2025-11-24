package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MessagesFragment extends Fragment implements ChatListAdapter.OnChatClickListener {

    private RecyclerView rv;
    private ChatListAdapter adapter;
    private DBHelper dbHelper;
    private String patientEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        SharedPreferences sp = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        patientEmail = sp.getString("user_email", null);

        rv = v.findViewById(R.id.recyclerMessages);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DBHelper();

        // Fetch doctors asynchronously
        dbHelper.getAllDoctors(new OnSuccessListener<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> doctors) {
                adapter = new ChatListAdapter(doctors, patientEmail, dbHelper, MessagesFragment.this);
                rv.setAdapter(adapter);
            }
        });

        return v;
    }

    @Override
    public void onChatClick(Doctor doctor) {
        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra("doctor_name", doctor.getName());
        startActivity(i);
    }
}
