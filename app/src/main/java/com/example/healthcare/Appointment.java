package com.example.healthcare;


public class Appointment {
    private String id;
    String doctorName, specialty, hospital, date, time, status;
    String doctorId; // or String if your doctor IDs are strings
    String patientId; // <-- add this
    public Appointment() {}
    public Appointment(String doctorId, String patientId, String date, String time, String status) {
        this.doctorId = doctorId;
        this.patientId = patientId; // <-- important
        this.date = date;
        this.time = time;
        this.status = status;
        this.doctorName = "";
        this.specialty = "";
        this.hospital = "";
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}