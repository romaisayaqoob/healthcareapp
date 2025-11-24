package com.example.healthcare;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper {

    private FirebaseFirestore db;

    public DBHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // ------------------ USERS ------------------

    public void registerUser(String name, String email, String password, String role, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Log.d("DBHelper", "Registering user: " + email + " with role: " + role);

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("password", password);
        user.put("role", role);

        db.collection("users").document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DBHelper", "User registered successfully: " + email);
                    onSuccess.onSuccess(aVoid);
                })
                .addOnFailureListener(e -> {
                    Log.e("DBHelper", "Failed to register user: " + e.getMessage(), e);
                    onFailure.onFailure(e);
                });
    }

    /*public void checkUserRole(String email, String password, OnSuccessListener<String> onSuccess) {
        Log.d("DBHelper", "Checking user role for: " + email);

        db.collection("users").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("DBHelper", "Firebase query successful");

                    if (documentSnapshot.exists()) {
                        Log.d("DBHelper", "Document exists for email: " + email);
                        String dbPass = documentSnapshot.getString("password");

                        if (dbPass != null && dbPass.equals(password)) {
                            String role = documentSnapshot.getString("role");
                            Log.d("DBHelper", "Password matched. Role: " + role);
                            onSuccess.onSuccess(role);
                        } else {
                            Log.w("DBHelper", "Password mismatch");
                            onSuccess.onSuccess(null);
                        }
                    } else {
                        Log.w("DBHelper", "No document found for email: " + email);
                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DBHelper", "Firebase error: " + e.getMessage(), e);
                    onSuccess.onSuccess(null); // Return null on error
                });
    }*/
    public void checkUserRole(String email, String password, OnSuccessListener<String> onSuccess) {
        if(email.equals("doctor@example.com") && password.equals("1234")){
            onSuccess.onSuccess("Doctor");
        } else if(email.equals("patient@example.com") && password.equals("1234")){
            onSuccess.onSuccess("Patient");
        } else {
            onSuccess.onSuccess(null);
        }
    }


    public void getNameByEmail(String email, OnSuccessListener<String> onSuccess) {
        db.collection("users").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onSuccess.onSuccess(documentSnapshot.getString("name"));
                    } else {
                        onSuccess.onSuccess(null);
                    }
                });
    }

    // ------------------ DOCTORS ------------------
// Fetch appointments for a specific doctor email
    public void getAppointmentsForDoctor(String doctorEmail, OnSuccessListener<List<Appointment>> onSuccess) {
        db.collection("appointments")
                .whereEqualTo("doctorEmail", doctorEmail) // <-- Make sure you store doctorEmail in appointments
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Appointment> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Appointment a = new Appointment(
                                doc.getString("doctorEmail"), // replace doctorId if using email
                                doc.getString("date"),
                                doc.getString("time"),
                                doc.getString("status")
                        );
                        list.add(a);
                    }
                    onSuccess.onSuccess(list);
                });
    }

    // Fetch doctor by email
    public void getDoctorByEmail(String email, OnSuccessListener<Doctor> onSuccess) {
        db.collection("doctors").whereEqualTo("email", email).get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        DocumentSnapshot doc = snapshots.getDocuments().get(0);
                        Doctor d = new Doctor(
                                doc.getString("email"),
                                doc.getString("name"),
                                doc.getString("specialization"),
                                doc.getString("hospital"),
                                doc.getLong("experience") != null ? doc.getLong("experience").intValue() : 0,
                                doc.getLong("patients") != null ? doc.getLong("patients").intValue() : 0,
                                doc.getDouble("rating")
                        );
                        onSuccess.onSuccess(d);
                    } else {
                        onSuccess.onSuccess(null);
                    }
                });
    }

    // Update doctor profile
    public void updateDoctorProfile(String email, String name, String hospital, String specialization,
                                    OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection("doctors").whereEqualTo("email", email).get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        String docId = snapshots.getDocuments().get(0).getId();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", name);
                        updates.put("hospital", hospital);
                        updates.put("specialization", specialization);

                        db.collection("doctors").document(docId).update(updates)
                                .addOnSuccessListener(onSuccess)
                                .addOnFailureListener(onFailure);
                    } else {
                        onFailure.onFailure(new Exception("Doctor not found"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void addDoctor(Doctor doctor, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("name", doctor.getName());
        docMap.put("specialization", doctor.getSpecialization());
        docMap.put("hospital", doctor.getHospital());
        docMap.put("experience", doctor.getExperience());
        docMap.put("patients", doctor.getPatients());
        docMap.put("rating", doctor.getRating());

        db.collection("doctors").add(docMap)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

   /* public void getAllDoctors(OnSuccessListener<List<Doctor>> onSuccess) {
        db.collection("doctors").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Doctor> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Doctor d = new Doctor(
                                doc.getId(),
                                doc.getString("name"),
                                doc.getString("specialization"),
                                doc.getString("hospital"),
                                doc.getLong("experience").intValue(),
                                doc.getLong("patients").intValue(),
                                doc.getDouble("rating")
                        );
                        list.add(d);
                    }
                    onSuccess.onSuccess(list);
                });
    }
*/

    public void getAllDoctors(OnSuccessListener<List<Doctor>> onSuccess) {
        List<Doctor> dummy = new ArrayList<>();
        dummy.add(new Doctor("1","Dr. A","Cardiology","City Hospital",10,100,4.5));
        dummy.add(new Doctor("2","Dr. B","Dermatology","Central Clinic",5,50,4.2));
        dummy.add(new Doctor("3","Dr. C","Pediatrics","Children's Hospital",8,80,4.8));
        onSuccess.onSuccess(dummy);
    }
    // ------------------ APPOINTMENTS ------------------

    public void createAppointment(Appointment a, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("doctorId", a.getDoctorId());
        appointment.put("date", a.getDate());
        appointment.put("time", a.getTime());
        appointment.put("status", a.getStatus());

        db.collection("appointments").add(appointment)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

   /* public void getAllAppointments(OnSuccessListener<List<Appointment>> onSuccess) {
        db.collection("appointments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Appointment> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Appointment a = new Appointment(
                                doc.getString("doctorId"),
                                doc.getString("date"),
                                doc.getString("time"),
                                doc.getString("status")
                        );
                        list.add(a);
                    }
                    onSuccess.onSuccess(list);
                });
    }*/
   public void getAllAppointments(OnSuccessListener<List<Appointment>> onSuccess) {
       List<Appointment> list = new ArrayList<>();
       list.add(new Appointment("1","2025-11-24","10:00 AM","UPCOMING"));
       list.add(new Appointment("2","2025-11-25","11:30 AM","COMPLETED"));
       onSuccess.onSuccess(list);
   }


    public void updateAppointmentStatus(String appointmentId, String newStatus, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection("appointments").document(appointmentId)
                .update("status", newStatus)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // ------------------ MESSAGES ------------------

    public void addMessage(Message msg,
                           OnSuccessListener<com.google.firebase.firestore.DocumentReference> onSuccess,
                           OnFailureListener onFailure) {
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("sender", msg.getSender());
        msgMap.put("receiver", msg.getReceiver());
        msgMap.put("doctorName", msg.getDoctorName());
        msgMap.put("message", msg.getMessage());
        msgMap.put("timestamp", msg.getTimestamp());
        msgMap.put("isRead", msg.isRead());

        db.collection("messages").add(msgMap)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void getMessages(String patientEmail, String doctorName, OnSuccessListener<List<Message>> onSuccess) {
        db.collection("messages")
                .whereEqualTo("receiver", patientEmail)
                .whereEqualTo("doctorName", doctorName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Message msg = new Message(
                                doc.getId(),
                                doc.getString("sender"),
                                doc.getString("receiver"),
                                doc.getString("doctorName"),
                                doc.getString("message"),
                                doc.getString("timestamp"),
                                doc.getBoolean("isRead")
                        );
                        list.add(msg);
                    }
                    onSuccess.onSuccess(list);
                });
    }
}
