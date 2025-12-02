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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment implements ChatListAdapter.OnChatClickListener {

    private RecyclerView rv;
    private ChatListAdapter adapter;
    private String patientName; // now we use name instead of UID
    private DatabaseReference dbRefChats;
    private DatabaseReference dbRefAppointments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        SharedPreferences sp = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        patientName = sp.getString("user_name", null); // get patient name

        rv = v.findViewById(R.id.recyclerMessages);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(getContext(), new ArrayList<>(), this);
        rv.setAdapter(adapter);

        dbRefChats = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("chats");
        dbRefAppointments = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("appointments");

        fetchDoctorChats();

        return v;
    }

    private void fetchDoctorChats() {
        // Step 1: get all doctors this patient has appointments with
        dbRefAppointments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Map<String, String> doctorMap = new HashMap<>(); // doctorId -> doctorName

                for (DataSnapshot apptSnap : snapshot.getChildren()) {
                    String patient = apptSnap.child("patientId").getValue(String.class);
                    if (patientName.equals(patient)) {
                        String doctorId = apptSnap.child("doctorId").getValue(String.class);
                        String doctorName = apptSnap.child("doctorName").getValue(String.class);
                        doctorMap.put(doctorId, doctorName);
                    }
                }

                fetchChatsForDoctors(doctorMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void fetchChatsForDoctors(Map<String, String> doctorMap) {
        dbRefChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatListItem> chatList = new ArrayList<>();

                for (Map.Entry<String, String> entry : doctorMap.entrySet()) {
                    String doctorId = entry.getKey();
                    String doctorName = entry.getValue();

                    String chatId = patientName + "_" + doctorId;
                    DataSnapshot chatSnap = snapshot.child(chatId);

                    String lastMessage = "Start a conversation";
                    long lastTimestamp = 0;

                    if (chatSnap.exists() && chatSnap.hasChild("messages")) {
                        for (DataSnapshot msgSnap : chatSnap.child("messages").getChildren()) {
                            Message m = msgSnap.getValue(Message.class);
                            if (m != null && m.getTimestamp() != null) {
                                long t = Long.parseLong(m.getTimestamp());
                                if (t > lastTimestamp) {
                                    lastTimestamp = t;
                                    lastMessage = m.getMessage();
                                }
                            }
                        }
                    }

                    chatList.add(new ChatListItem(doctorId, doctorName, lastMessage, lastTimestamp));
                }

                adapter.updateList(chatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onChatClick(ChatListItem item) {
        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra("doctor_uid", item.getDoctorUid());
        i.putExtra("doctor_name", item.getDoctorName());
        startActivity(i);
    }
}
