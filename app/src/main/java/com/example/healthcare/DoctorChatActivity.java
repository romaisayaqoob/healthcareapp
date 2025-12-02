package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText edtMessage;
    Button btnSend;

    ArrayList<Message> messageList;
    DoctorMessageAdapter adapter; // custom adapter for doctor

    DatabaseReference chatRef;

    String doctorId, patientId, chatId, patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        patientId = getIntent().getStringExtra("patientId");
        patientName = getIntent().getStringExtra("patientName");
        doctorId = getIntent().getStringExtra("doctorId");

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView txtDoctorName = findViewById(R.id.txtDoctorName);
        String patientName = getIntent().getStringExtra("patientName");
        String formattedName = capitalizeWords(patientName);
        txtDoctorName.setText(formattedName);
        chatId = patientId + "_" + doctorId;
        btnBack.setOnClickListener(v -> finish());
        setTitle(patientName);

        recyclerView = findViewById(R.id.recyclerMessages);
        edtMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        messageList = new ArrayList<>();
        adapter = new DoctorMessageAdapter(messageList, doctorId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        chatRef = FirebaseDatabase.getInstance(
                        "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
                )
                .getReference("chats")
                .child(chatId)
                .child("messages");

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message m = ds.getValue(Message.class);
                    if (m != null) messageList.add(m);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void sendMessage() {
        String text = edtMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String msgId = chatRef.push().getKey();

        Message message = new Message(
                msgId,
                doctorId,       // sender
                patientId,      // receiver
                "",             // doctorName optional
                text,
                String.valueOf(System.currentTimeMillis()),
                false
        );

        chatRef.child(msgId).setValue(message);

        edtMessage.setText("");
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
