package com.example.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private MessageAdapter adapter;
    private DBHelper dbHelper;

    private String patientId = "PATIENT_ID"; // get from SharedPreferences/session
    private String doctorId;
    private String chatId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // your chat XML
        SharedPreferences sp = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        patientId = sp.getString("user_uid", null);

        doctorId = getIntent().getStringExtra("doctor_uid");
        chatId = patientId + "_" + doctorId;

        recyclerView = findViewById(R.id.recyclerMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(patientId);
        recyclerView.setAdapter(adapter);

        dbHelper = new DBHelper();

        // Listen for messages in real-time
        /*dbHelper.listenForMessages(chatId, new DBHelper.OnMessagesReceived() {
            @Override
            public void onReceived(List<Message> messages) {
                adapter.updateList(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });*/

        TextView txtDoctorName = findViewById(R.id.txtDoctorName);
        ImageView btnBack = findViewById(R.id.btnBack);
        String doctorName = getIntent().getStringExtra("doctor_name");
        String formattedName = "Dr. " + capitalizeWords(doctorName);
        txtDoctorName.setText(formattedName);

        btnBack.setOnClickListener(v -> finish());


        // Send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etMessage.getText().toString().trim();
                if (!text.isEmpty()) {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    Message msg = new Message(patientId, doctorId, text, timestamp);
                    dbHelper.sendMessage(chatId, msg);
                    etMessage.setText("");
                }
            }
        });

        listenForMessages();
    }
    private void listenForMessages() {

        DatabaseReference ref = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("chats")
                .child(chatId)
                .child("messages");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Message> messages = new java.util.ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message m = ds.getValue(Message.class);
                    if (m != null) messages.add(m);
                }

                adapter.updateList(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
