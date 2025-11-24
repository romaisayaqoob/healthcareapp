// =============================
// AppointmentAdapter.java
// =============================
package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.VH> {

    public interface OnAppointmentActionListener {
        void onReschedule(Appointment appointment);
        void onViewDetails(Appointment appointment);
    }

    private List<Appointment> items;
    private List<Appointment> fullList;
    private OnAppointmentActionListener listener;

    public AppointmentAdapter(List<Appointment> appointments, OnAppointmentActionListener listener) {
        this.items = new ArrayList<>(appointments);
        this.fullList = new ArrayList<>(appointments);
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Appointment a = items.get(position);

        holder.txtDoctor.setText(a.getDoctorName());
        holder.txtSpec.setText(a.getSpecialty() + " • " + a.getHospital());
        holder.txtDateTime.setText(a.getDate() + " at " + a.getTime());

        holder.txtStatus.setText(a.getStatus());

        switch (a.getStatus()) {
            case "UPCOMING": holder.txtStatus.setBackgroundResource(R.drawable.status_blue); break;
            case "COMPLETED": holder.txtStatus.setBackgroundResource(R.drawable.status_green); break;
            case "CANCELLED": holder.txtStatus.setBackgroundResource(R.drawable.status_red); break;
        }

        holder.btnAction.setVisibility(View.GONE);

        if (a.getStatus().equals("UPCOMING")) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Reschedule");
            holder.btnAction.setOnClickListener(v -> listener.onReschedule(a));
        }

        if (a.getStatus().equals("COMPLETED")) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Details");
            holder.btnAction.setOnClickListener(v -> listener.onViewDetails(a));
        }
    }

    public void filterByStatus(String status) {
        items.clear();

        if (status.equals("ALL")) {
            items.addAll(fullList);
        } else {
            for (Appointment a : fullList) {
                if (a.getStatus().equals(status)) {
                    items.add(a);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void search(String text) {
        items.clear();
        text = text.toLowerCase();

        for (Appointment a : fullList) {
            if (a.getDoctorName().toLowerCase().contains(text) ||
                    a.getSpecialty().toLowerCase().contains(text)) {
                items.add(a);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtDoctor, txtSpec, txtDateTime, txtStatus, txtSpecialty, txtHospital, txtDate, txtTime;
        Button btnAction;

        VH(View itemView) {
            super(itemView);
            txtDoctor = itemView.findViewById(R.id.txtDoctor);
            txtSpecialty = itemView.findViewById(R.id.txtSpecialty);
            txtHospital = itemView.findViewById(R.id.txtHospital);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnAction = itemView.findViewById(R.id.btnAction);

        }
    }
}



