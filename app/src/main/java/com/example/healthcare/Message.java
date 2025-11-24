package com.example.healthcare;

public class Message {
    private String id;
    private String sender;
    private String receiver;
    private String doctorName;
    private String message;
    private String timestamp;
    private boolean isRead;

    public Message(String id, String sender, String receiver, String doctorName,
                   String message, String timestamp, boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.doctorName = doctorName;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getDoctorName() { return doctorName; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }

    // Setters
    public void setRead(boolean read) { isRead = read; }
}
