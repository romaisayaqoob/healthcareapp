package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    private List<Doctor> doctorList;
    private List<Doctor> fullList; // for filtering
    private OnDoctorClickListener listener;

    public DoctorAdapter(List<Doctor> doctorList, OnDoctorClickListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
        this.fullList = new ArrayList<>(doctorList); // initialize for filtering
    }

    public void updateList(List<Doctor> list) {
        doctorList.clear();
        doctorList.addAll(list);
        fullList.clear();
        fullList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor d = doctorList.get(position);
        holder.name.setText(d.getName());
        holder.specialty.setText(d.getSpecialization() + " • " + d.getHospital());

        holder.itemView.setOnClickListener(v -> listener.onDoctorClick(d));
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            specialty = itemView.findViewById(R.id.text_spec);
        }
    }

    public void filter(String text) {
        doctorList.clear();
        if (text.isEmpty()) {
            doctorList.addAll(fullList);
        } else {
            String lower = text.toLowerCase();
            for (Doctor d : fullList) {
                if (d.getName().toLowerCase().contains(lower) ||
                        d.getSpecialization().toLowerCase().contains(lower) ||
                        d.getHospital().toLowerCase().contains(lower)) {
                    doctorList.add(d);
                }
            }
        }
        notifyDataSetChanged();
    }
}
