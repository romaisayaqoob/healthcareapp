package com.example.healthcare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EditText etMessage;
    private Button btnSend;
    private DBHelper dbHelper;
    private String patientEmail, doctorName;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        doctorName = getIntent().getStringExtra("doctor_name");
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        patientEmail = sp.getString("user_email", null);

        dbHelper = new DBHelper();

        rv = findViewById(R.id.recyclerMessages);
        rv.setLayoutManager(new LinearLayoutManager(this));

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Initialize adapter with empty list
        adapter = new MessageAdapter(patientEmail);

        rv.setAdapter(adapter);

        loadMessages();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

                Message msg = new Message(
                        null, // Firestore will assign ID
                        "patient",
                        patientEmail,
                        doctorName,
                        text,
                        timestamp,
                        true
                );

                dbHelper.addMessage(msg,
                        docRef -> {
                            etMessage.setText("");  // clear input
                            loadMessages();         // reload messages
                        },
                        e -> Log.e("ChatActivity", "Failed to send message", e)
                );
            }
        });
    }

    private void loadMessages() {
        dbHelper.getMessages(patientEmail, doctorName, messagesList -> {
            adapter.updateList(messagesList);
            rv.scrollToPosition(messagesList.size() - 1);
        });
    }
}
