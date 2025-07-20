package com.example.irr_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.ViewHolder> {

    private Context context;
    private List<Internship> internships;
    private OnInternshipClickListener listener;

    public interface OnInternshipClickListener {
        void onInternshipClick(Internship internship);
    }

    public InternshipAdapter(Context context, List<Internship> internships, OnInternshipClickListener listener) {
        this.context = context;
        this.internships = internships;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internship, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Internship internship = internships.get(position);
        holder.titleTextView.setText(internship.getTitle());
        holder.companyTextView.setText("Công ty: " + internship.getCompanyName());
        holder.locationTextView.setText("Địa điểm: " + internship.getLocation());
        holder.durationTextView.setText("Thời gian: " + internship.getDuration());

        holder.itemView.setOnClickListener(v -> listener.onInternshipClick(internship));
    }

    @Override
    public int getItemCount() {
        return internships.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView companyTextView;
        TextView locationTextView;
        TextView durationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.internship_title_textview);
            companyTextView = itemView.findViewById(R.id.company_name_textview);
            locationTextView = itemView.findViewById(R.id.location_textview);
            durationTextView = itemView.findViewById(R.id.duration_textview);
        }
    }
}