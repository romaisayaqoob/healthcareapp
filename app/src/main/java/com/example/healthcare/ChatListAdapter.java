package com.example.healthcare;

import android.content.Context;
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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.VH> {

    private Context context;
    private List<ChatListItem> chatList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatListItem item);
    }

    public ChatListAdapter(Context context, List<ChatListItem> chatList, OnChatClickListener listener) {
        this.context = context;
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatListItem item = chatList.get(position);
        holder.name.setText(item.getDoctorName());
        holder.lastMessage.setText(item.getLastMessage());
        String initial = item.getDoctorName() != null && !item.getDoctorName().isEmpty()
                ? item.getDoctorName().substring(0, 1).toUpperCase()
                : "?";
        holder.textInitial.setText(initial);
        if (item.getLastTimestamp() > 0) {
            String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date(item.getLastTimestamp()));
            holder.time.setText(time);
        } else {
            holder.time.setText("");
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(item));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateList(List<ChatListItem> newList) {
        chatList.clear();
        chatList.addAll(newList);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, lastMessage, time, textInitial;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            lastMessage = itemView.findViewById(R.id.text_last_message);
            time = itemView.findViewById(R.id.text_time);
            textInitial = itemView.findViewById(R.id.text_initial);
        }
    }
}
