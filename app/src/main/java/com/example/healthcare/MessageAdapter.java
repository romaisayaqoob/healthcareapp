package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    private List<Message> messages;
    private String currentUserUid;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public MessageAdapter(String currentUserUid) {
        this.messages = new ArrayList<>();
        this.currentUserUid = currentUserUid;
    }

    // 1️⃣ Decide which layout to use for this position
    @Override
    public int getItemViewType(int position) {
        Message m = messages.get(position);
        if (m.getSenderUid().equals(currentUserUid)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // 2️⃣ Inflate the correct layout based on view type
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_SENT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_left, parent, false);
        }
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Message m = messages.get(position);
        holder.message.setText(m.getMessage());

        // timestamp formatting
        if (m.getTimestamp() != null) {
            try {
                long t = Long.parseLong(m.getTimestamp());
                String formatted = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                        .format(new Date(t));
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

    public void updateList(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView message;
        TextView time;
        VH(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.txtMessage);  // <- corrected
            time = itemView.findViewById(R.id.txtTime);        // <- corrected
        }
    }
}

