package com.example.healthcare;

public class Doctor extends User {

    private String specialization;
    private String hospital;
    private String experience;       // e.g., "5 years"
    private String qualification;    // e.g., "MBBS, MD"
  /*  private int patients;            // optional
    private double rating;           // optional*/

    public Doctor() {
        // Required empty constructor for Firebase
    }

    public Doctor(
            String uid,
            String name,
            String email,
            String specialization,
            String hospital,
            String experience,
            String qualification
    ) {
        super(uid, name, email, "Doctor"); // role is always Doctor
        this.specialization = specialization;
        this.hospital = hospital;
        this.experience = experience;
        this.qualification = qualification;
       /* this.patients = 0;
        this.rating = 0.0;*/
    }

    // Getters
    public String getSpecialization() { return specialization; }
    public String getHospital() { return hospital; }
    public String getExperience() { return experience; }
    public String getQualification() { return qualification; }
    /*public int getPatients() { return patients; }
    public double getRating() { return rating; }*/

    // Setters
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setQualification(String qualification) { this.qualification = qualification; }
/*    public void setPatients(int patients) { this.patients = patients; }
    public void setRating(double rating) { this.rating = rating; }*/
}
