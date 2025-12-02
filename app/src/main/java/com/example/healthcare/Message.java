package com.example.healthcare;

public class Message {
    private String id;           // Firebase-generated ID
    private String senderUid;    // UID of sender (patient or doctor)
    private String receiverUid;  // UID of receiver (patient or doctor)
    private String doctorName;   // Optional, for display purposes
    private String message;      // Message text
    private String timestamp;    // "yyyy-MM-dd HH:mm"
    private boolean isRead;      // Message read status

    // Full constructor
    public Message(String id, String senderUid, String receiverUid, String doctorName,
                   String message, String timestamp, boolean isRead) {
        this.id = id;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.doctorName = doctorName;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Simple constructor for sending new messages
    public Message(String senderUid, String receiverUid, String message, String timestamp) {
        this.id = null;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.doctorName = "";
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
    }

    // Empty constructor needed for Firebase
    public Message() {}

    // Getters
    public String getId() { return id; }
    public String getSenderUid() { return senderUid; }
    public String getReceiverUid() { return receiverUid; }
    public String getDoctorName() { return doctorName; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSenderUid(String senderUid) { this.senderUid = senderUid; }
    public void setReceiverUid(String receiverUid) { this.receiverUid = receiverUid; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { isRead = read; }
}
