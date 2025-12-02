package com.example.healthcare;

public class ChatListItem {
    private String doctorUid;
    private String doctorName;
    private String lastMessage;
    private long lastTimestamp;

    public ChatListItem(String doctorUid, String doctorName, String lastMessage, long lastTimestamp) {
        this.doctorUid = doctorUid;
        this.doctorName = doctorName;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
    }

    public String getDoctorUid() { return doctorUid; }
    public String getDoctorName() { return doctorName; }
    public String getLastMessage() { return lastMessage; }
    public long getLastTimestamp() { return lastTimestamp; }
}
