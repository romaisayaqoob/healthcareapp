package com.example.healthcare;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper {

    private final DatabaseReference db;

    public DBHelper() {
        db = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference();
    }

    // ================== DOCTORS ==================

    public void addDoctor(String uid, Doctor doctor,
                          OnSuccessListener<Void> onSuccess,
                          OnFailureListener onFailure) {
        db.child("doctors").child(uid)
                .setValue(doctor)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void getDoctorByUid(String uid, OnSuccessListener<Doctor> onSuccess) {

        DatabaseReference root = FirebaseDatabase.getInstance(
                "https://healthcareapp-dad87-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference();

        // 1) Get doctor data from "doctors"
        root.child("doctors").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot doctorSnap) {

                Doctor doctor = doctorSnap.getValue(Doctor.class);

                if (doctor == null) {
                    onSuccess.onSuccess(null);
                    return;
                }

                // 2) Get user data from "users" for name & email
                root.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnap) {
                        String name = userSnap.child("name").getValue(String.class);
                        String email = userSnap.child("email").getValue(String.class);

                        doctor.setName(name);
                        doctor.setEmail(email);
                        doctor.setId(uid);

                        onSuccess.onSuccess(doctor);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onSuccess.onSuccess(doctor);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onSuccess.onSuccess(null);
            }
        });
    }

    public void updateDoctorProfile(String uid, String hospital, String specialization,
                                    String experience, String qualification,
                                    OnSuccessListener<Void> onSuccess,
                                    OnFailureListener onFailure) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("hospital", hospital);
        updates.put("specialization", specialization);
        updates.put("experience", experience);
        updates.put("qualification", qualification);

        db.child("doctors").child(uid)
                .updateChildren(updates)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void getAllDoctors(OnSuccessListener<List<Doctor>> onSuccess) {
        db.child("doctors")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Doctor> list = new ArrayList<>();
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            Doctor d = doc.getValue(Doctor.class);
                            if (d != null) list.add(d);
                        }
                        onSuccess.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // ================== PATIENTS ==================

    public void addPatient(String uid, User patient,
                           OnSuccessListener<Void> onSuccess,
                           OnFailureListener onFailure) {
        db.child("patients").child(uid)
                .setValue(patient)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getPatientByUid(String uid, OnSuccessListener<User> onSuccess) {
        db.child("patients").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            onSuccess.onSuccess(null);
                            return;
                        }
                        User u = snapshot.getValue(User.class);
                        onSuccess.onSuccess(u);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onSuccess.onSuccess(null);
                    }
                });
    }

   /* public void createAppointment(Appointment appointment,
                                  OnSuccessListener<Void> onSuccess,
                                  OnFailureListener onFailure) {

        // Generate a new push ID for appointment
        String appointmentId = db.child("appointments").push().getKey();
        if (appointmentId == null) {
            onFailure.onFailure(new Exception("Failed to generate appointment ID"));
            return;
        }

        // Set values
        Map<String, Object> values = new HashMap<>();
        values.put("id", appointmentId);
        values.put("doctorId", appointment.getDoctorId());
        values.put("patientUid", appointment.getPatientUid()); // must be set before calling
        values.put("date", appointment.getDate());
        values.put("time", appointment.getTime());
        values.put("status", appointment.getStatus());

        db.child("appointments").child(appointmentId)
                .setValue(values)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
*/
    // -------------------- Add Message --------------------
   public void sendMessage(String chatId, Message message) {
       String messageId = db.child("chats").child(chatId).child("messages").push().getKey();
       db.child("chats").child(chatId).child("messages").child(messageId).setValue(message);
   }

    public void listenForMessages(String chatId, final OnMessagesReceived listener) {
        db.child("chats").child(chatId).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Message> messages = new java.util.ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Message msg = ds.getValue(Message.class);
                            if (msg != null) messages.add(msg);
                        }
                        listener.onReceived(messages);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }

    public void deleteMessage(String chatId, String messageId, String userId) {
        db.child("chats").child(chatId).child("messages").child(messageId)
                .child("deletedBy").push().setValue(userId);
    }

    public interface OnMessagesReceived {
        void onReceived(List<Message> messages);
    }
    // ================== APPOINTMENTS ==================

    public void addAppointment(Appointment appointment,
                               OnSuccessListener<Void> onSuccess,
                               OnFailureListener onFailure) {
        String key = db.child("appointments").push().getKey();
        db.child("appointments").child(key)
                .setValue(appointment)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAppointmentsForDoctor(String doctorUid, OnSuccessListener<List<Appointment>> onSuccess) {
        db.child("appointments")
                .orderByChild("doctorId")
                .equalTo(doctorUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Appointment> list = new ArrayList<>();
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            Appointment a = doc.getValue(Appointment.class);
                            if (a != null) list.add(a);
                        }
                        onSuccess.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public void getAppointmentsForPatient(String patientUid, OnSuccessListener<List<Appointment>> onSuccess) {
        db.child("appointments")
                .orderByChild("patientId")
                .equalTo(patientUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Appointment> list = new ArrayList<>();
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            Appointment a = doc.getValue(Appointment.class);
                            if (a != null) list.add(a);
                        }
                        onSuccess.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}
