package com.example.healthcare;

public class Doctor {
    private String id;
    private String name;
    private String specialization;
    private String hospital;
    private int experience;
    private int patients;
    private double rating;

    public Doctor() {}
    public Doctor(String id, String name, String specialization, String hospital, int experience, int patients, double rating) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.hospital = hospital;
        this.experience = experience;
        this.patients = patients;
        this.rating = rating;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getHospital() { return hospital; }
    public int getExperience() { return experience; }
    public int getPatients() { return patients; }
    public double getRating() { return rating; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setPatients(int patients) { this.patients = patients; }
    public void setRating(double rating) { this.rating = rating; }
}
