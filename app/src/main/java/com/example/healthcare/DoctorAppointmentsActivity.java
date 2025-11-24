package com.example.healthcare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

        listView = findViewById(R.id.listViewAppointments);
        dbHelper = new DBHelper();

        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String doctorEmail = sp.getString("user_email", null);

        if (doctorEmail == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch appointments for this doctor
        dbHelper.getAppointmentsForDoctor(doctorEmail, appointments -> {
            List<String> items = new ArrayList<>();
            for (Appointment a : appointments) {
                items.add(a.getDate() + " | " + a.getTime() + " | " + a.getStatus());
            }
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
        });
    }
}
