package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    private List<Message> messages;
    private String patientEmail;

    public MessageAdapter(String patientEmail) {
        this.messages = new ArrayList<>();
        this.patientEmail = patientEmail;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Message m = messages.get(position);

        holder.message.setText(m.getMessage());

        if ("patient".equals(m.getSender())) {
            holder.message.setBackgroundResource(R.drawable.bubble_right);
            holder.message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            holder.message.setBackgroundResource(R.drawable.bubble_left);
            holder.message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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
        VH(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.textMessage);
        }
    }
}
