package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorMessageAdapter extends RecyclerView.Adapter<DoctorMessageAdapter.VH> {

    private List<Message> messages;
    private String currentUserUid;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public DoctorMessageAdapter(List<Message> messages, String currentUserUid) {
        this.messages = messages;
        this.currentUserUid = currentUserUid;
    }

    @Override
    public int getItemViewType(int position) {
        Message m = messages.get(position);
        return m.getSenderUid().equals(currentUserUid) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == VIEW_TYPE_SENT ? R.layout.item_message_right : R.layout.item_message_left,
                        parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Message m = messages.get(position);
        holder.message.setText(m.getMessage());

        if (m.getTimestamp() != null) {
            try {
                long t = Long.parseLong(m.getTimestamp());
                String formatted = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(t));
                holder.time.setText(formatted);
            } catch (Exception e) {
                holder.time.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView message, time;
        VH(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.txtMessage);
            time = itemView.findViewById(R.id.txtTime);
        }
    }
}
