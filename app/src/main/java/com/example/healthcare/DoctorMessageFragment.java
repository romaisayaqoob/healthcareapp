package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DoctorMessageFragment extends Fragment implements ChatListAdapter.OnChatClickListener {

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private DatabaseReference appointmentsRef;
    private DatabaseReference chatsRef;
    private String doctorId;

    public DoctorMessageFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_doctor_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        SharedPreferences sp = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        doctorId = sp.getString("user_uid", null);

        appointmentsRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("appointments");

        chatsRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("chats");

        loadDoctorChats();

        return view;
    }

    /*private void loadDoctorChats() {
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getContext(), "Appointments snapshot received: " + snapshot.getChildrenCount(), Toast.LENGTH_LONG).show();
                List<ChatListItem> chatList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Appointment appt = ds.getValue(Appointment.class);
                    if (appt != null && doctorId.equals(appt.getDoctorId())) {

                        String patientName = appt.getPatientId(); // This is actually the patient's name
                        Toast.makeText(getContext(), "Processing appointment for: " + patientName, Toast.LENGTH_SHORT).show();

                        // Use patient name directly to construct chatId
                        String chatId = patientName + "_" + doctorId; // Same format as patient uses
                        Toast.makeText(getContext(), "ChatId: " + chatId, Toast.LENGTH_SHORT).show();

                        // Fetch last message
                        chatsRef.child(chatId).child("messages").get()
                                .addOnSuccessListener(msgSnap -> {
                                    String lastMessage = "Start a conversation";
                                    long lastTimestamp = 0;

                                    for (DataSnapshot mds : msgSnap.getChildren()) {
                                        Message m = mds.getValue(Message.class);
                                        if (m != null && m.getTimestamp() != null) {
                                            long t = Long.parseLong(m.getTimestamp());
                                            if (t > lastTimestamp) {
                                                lastTimestamp = t;
                                                lastMessage = m.getMessage();
                                            }
                                        }
                                    }

                                    Toast.makeText(getContext(),
                                            "Last message: " + lastMessage + " Timestamp: " + lastTimestamp,
                                            Toast.LENGTH_SHORT).show();

                                    chatList.add(new ChatListItem(
                                            patientName,  // Store patient name, not UID
                                            patientName,  // Display patient name
                                            lastMessage,
                                            lastTimestamp
                                    ));
                                    adapter.updateList(chatList);

                                    Toast.makeText(getContext(),
                                            "Chat added to list for: " + patientName,
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(),
                                        "Failed to fetch messages for chatId: " + chatId,
                                        Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Appointments fetch cancelled: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onChatClick(ChatListItem item) {
        Toast.makeText(getContext(),
                "patientName: " + item.getDoctorUid() + "\ndoctorId: " + doctorId,
                Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), DoctorChatActivity.class);
        i.putExtra("patientId", item.getDoctorUid()); // This will be patient name
        i.putExtra("patientName", item.getDoctorName()); // This will be patient name
        i.putExtra("doctorId", doctorId);
        startActivity(i);
    }*/

    private void loadDoctorChats() {
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getContext(), "Appointments snapshot received: " + snapshot.getChildrenCount(), Toast.LENGTH_LONG).show();

                // Create a list to track all async operations
                final List<ChatListItem> chatList = new ArrayList<>();
                final int[] pendingQueries = {0}; // Counter for pending queries

                // Count total appointments first
                int totalAppointments = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Appointment appt = ds.getValue(Appointment.class);
                    if (appt != null && doctorId.equals(appt.getDoctorId())) {
                        totalAppointments++;
                    }
                }

                final int totalCount = totalAppointments;

                if (totalCount == 0) {
                    Toast.makeText(getContext(), "No appointments found", Toast.LENGTH_SHORT).show();
                    adapter.updateList(chatList);
                    return;
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Appointment appt = ds.getValue(Appointment.class);
                    if (appt != null && doctorId.equals(appt.getDoctorId())) {

                        String patientName = appt.getPatientId();
                        Toast.makeText(getContext(), "Processing appointment for: " + patientName, Toast.LENGTH_SHORT).show();

                        pendingQueries[0]++;

                        // Query users table to get the patient UID
                        DatabaseReference usersRef = FirebaseDatabase.getInstance(
                                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
                        ).getReference("users");

                        usersRef.orderByChild("name").equalTo(patientName).get()
                                .addOnSuccessListener(userSnap -> {
                                    if (userSnap.exists()) {
                                        for (DataSnapshot dsUser : userSnap.getChildren()) {
                                            String patientUid = dsUser.getKey();

                                            Toast.makeText(getContext(),
                                                    "Found user: " + patientName + " UID: " + patientUid,
                                                    Toast.LENGTH_SHORT).show();

                                            String chatId = patientUid + "_" + doctorId;
                                            Toast.makeText(getContext(), "ChatId: " + chatId, Toast.LENGTH_SHORT).show();

                                            // Fetch last message
                                            // Fetch last message
                                            chatsRef.child(chatId).child("messages").get()
                                                    .addOnSuccessListener(msgSnap -> {
                                                        String lastMessage = "Start a conversation";
                                                        long lastTimestamp = 0;

                                                        for (DataSnapshot mds : msgSnap.getChildren()) {
                                                            Message m = mds.getValue(Message.class);
                                                            if (m != null && m.getTimestamp() != null) {
                                                                try {
                                                                    // Try to parse as long first (Unix timestamp)
                                                                    long t = Long.parseLong(m.getTimestamp());
                                                                    if (t > lastTimestamp) {
                                                                        lastTimestamp = t;
                                                                        lastMessage = m.getMessage();
                                                                    }
                                                                } catch (NumberFormatException e) {
                                                                    // If it's not a number, it's a date string like "2025-11-30 19:30"
                                                                    // Convert date string to timestamp
                                                                    try {
                                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                                                                        Date date = sdf.parse(m.getTimestamp());
                                                                        if (date != null) {
                                                                            long t = date.getTime();
                                                                            if (t > lastTimestamp) {
                                                                                lastTimestamp = t;
                                                                                lastMessage = m.getMessage();
                                                                            }
                                                                        }
                                                                    } catch (Exception parseError) {
                                                                        // If parsing fails, just use current time
                                                                        lastTimestamp = System.currentTimeMillis();
                                                                        lastMessage = m.getMessage();
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        synchronized (chatList) {
                                                            chatList.add(new ChatListItem(
                                                                    patientUid,
                                                                    patientName,
                                                                    lastMessage,
                                                                    lastTimestamp
                                                            ));

                                                            // Update adapter only when all queries are done
                                                            pendingQueries[0]--;
                                                            if (pendingQueries[0] == 0) {
                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(() -> {
                                                                        adapter.updateList(chatList);
                                                                        Toast.makeText(getContext(),
                                                                                "Loaded " + chatList.size() + " chats",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(getContext(),
                                                                "Failed to fetch messages for chatId: " + chatId,
                                                                Toast.LENGTH_SHORT).show();
                                                        pendingQueries[0]--;
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(getContext(),
                                                "No user found for: " + patientName,
                                                Toast.LENGTH_SHORT).show();
                                        pendingQueries[0]--;
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(),
                                            "Failed to query users: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    pendingQueries[0]--;
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Appointments fetch cancelled: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onChatClick(ChatListItem item) {
        Intent i = new Intent(getContext(), DoctorChatActivity.class);
        i.putExtra("patientId", item.getDoctorUid());      // This is now patient UID
        i.putExtra("patientName", item.getDoctorName());   // This is patient name
        i.putExtra("doctorId", doctorId);
        startActivity(i);
    }
}
