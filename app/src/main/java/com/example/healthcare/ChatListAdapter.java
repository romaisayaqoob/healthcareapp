package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.VH> {

    public interface OnChatClickListener { void onChatClick(Doctor doctor); }

    private List<Doctor> doctors;
    private String patientEmail;
    private DBHelper dbHelper;
    private OnChatClickListener listener;

    public ChatListAdapter(List<Doctor> doctors, String patientEmail, DBHelper dbHelper, OnChatClickListener listener) {
        this.doctors = doctors;
        this.patientEmail = patientEmail;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Doctor d = doctors.get(position);
        holder.name.setText(d.getName());
        holder.message.setText("Loading..."); // default text while fetching

        // Firestore async call to get last message
        dbHelper.getMessages(patientEmail, d.getName(), messagesList -> {
            if (messagesList != null && !messagesList.isEmpty()) {
                // get the last message
                Message lastMsg = messagesList.get(messagesList.size() - 1);
                holder.message.setText(lastMsg.getMessage());
            } else {
                holder.message.setText("No messages yet");
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onChatClick(d));
    }

    @Override
    public int getItemCount() { return doctors.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, message;
        VH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            message = itemView.findViewById(R.id.text_last_message);
        }
    }
}
