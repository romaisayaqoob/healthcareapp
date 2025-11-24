package com.example.healthcare;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    Context context;
    List<Appointment> list;

    public ScheduleAdapter(Context context, List<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<Appointment> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment a = list.get(position);

        holder.doctor.setText(a.getDoctorName());
        holder.specialty.setText(a.getSpecialty());
        holder.hospital.setText(a.getHospital());
        holder.date.setText(a.getDate());
        holder.time.setText(a.getTime());
        holder.status.setText(a.getStatus());

        if (a.getStatus().equals("UPCOMING")) {
            holder.status.setBackgroundColor(Color.BLUE);
            holder.actionBtn.setText("Reschedule");
            holder.actionBtn.setVisibility(View.VISIBLE);
        } else if (a.getStatus().equals("COMPLETED")) {
            holder.status.setBackgroundColor(Color.GREEN);
            holder.actionBtn.setText("Details");
            holder.actionBtn.setVisibility(View.VISIBLE);
        } else {
            holder.status.setBackgroundColor(Color.RED);
            holder.actionBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctor, specialty, hospital, date, time, status;
        Button actionBtn;


        ViewHolder(View itemView) {
            super(itemView);
            doctor = itemView.findViewById(R.id.txtDoctor);
            specialty = itemView.findViewById(R.id.txtSpecialty);
            hospital = itemView.findViewById(R.id.txtHospital);
            date = itemView.findViewById(R.id.txtDate);
            time = itemView.findViewById(R.id.txtTime);
            status = itemView.findViewById(R.id.txtStatus);
            actionBtn = itemView.findViewById(R.id.btnAction);
        }
    }
}


