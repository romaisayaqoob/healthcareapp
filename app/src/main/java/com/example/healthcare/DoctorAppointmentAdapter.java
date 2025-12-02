package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.VH> {

    public interface OnAppointmentActionListener {
        void onAccept(Appointment appointment);
        void onComplete(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    private List<Appointment> appointments;
    private OnAppointmentActionListener listener;

    public DoctorAppointmentAdapter(List<Appointment> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_appointment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Appointment appt = appointments.get(position);

        holder.tvPatientName.setText(appt.getPatientId()); // optionally fetch patient name from DB
        holder.tvDateTime.setText(appt.getDate() + " at " + appt.getTime());
        holder.tvStatus.setText(appt.getStatus());

        // Show/Hide buttons based on status
        holder.btnAccept.setVisibility(appt.getStatus().equals("REQUESTED") ? View.VISIBLE : View.GONE);
        holder.btnComplete.setVisibility(appt.getStatus().equals("APPROVED") ? View.VISIBLE : View.GONE);
        holder.btnCancel.setVisibility(!appt.getStatus().equals("CANCELLED") ? View.VISIBLE : View.GONE);

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(appt));
        holder.btnComplete.setOnClickListener(v -> listener.onComplete(appt));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(appt));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDateTime, tvStatus;
        Button btnAccept, btnComplete, btnCancel;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
