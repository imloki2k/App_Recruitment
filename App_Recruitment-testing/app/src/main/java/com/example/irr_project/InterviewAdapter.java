package com.example.irr_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InterviewAdapter extends RecyclerView.Adapter<InterviewAdapter.ViewHolder> {

    private Context context;
    private List<Interview> interviews;
    private OnInterviewActionListener listener;
    private String userRole;

    public interface OnInterviewActionListener {
        void onConfirmClick(Interview interview, int position);
        void onDeclineClick(Interview interview, int position);
        void onProposeClick(Interview interview, int position);
    }

    public InterviewAdapter(Context context, List<Interview> interviews, OnInterviewActionListener listener, String userRole) {
        this.context = context;
        this.interviews = interviews;
        this.listener = listener;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Interview interview = interviews.get(position);
        holder.internshipTitleTextView.setText(interview.getInternshipTitle());
        holder.timeTextView.setText("Thời gian: " + interview.getTime());
        holder.statusTextView.setText("Trạng thái: " + interview.getStatus());

        // Hiển thị/ẩn nút dựa trên vai trò và trạng thái
        if (userRole.equals("student") && interview.getStatus().equals(Interview.Status.PROPOSED.toString())) {
            holder.confirmButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
            holder.proposeButton.setVisibility(View.GONE);
        } else if (userRole.equals("recruiter")) {
            holder.confirmButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.proposeButton.setVisibility(View.VISIBLE);
        } else {
            holder.confirmButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.proposeButton.setVisibility(View.GONE);
        }

        holder.confirmButton.setOnClickListener(v -> listener.onConfirmClick(interview, position));
        holder.declineButton.setOnClickListener(v -> listener.onDeclineClick(interview, position));
        holder.proposeButton.setOnClickListener(v -> listener.onProposeClick(interview, position));
    }

    @Override
    public int getItemCount() {
        return interviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView internshipTitleTextView;
        TextView timeTextView;
        TextView statusTextView;
        Button confirmButton;
        Button declineButton;
        Button proposeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            internshipTitleTextView = itemView.findViewById(R.id.interview_internship_title_textview);
            timeTextView = itemView.findViewById(R.id.interview_time_textview);
            statusTextView = itemView.findViewById(R.id.interview_status_textview);
            confirmButton = itemView.findViewById(R.id.confirm_button);
            declineButton = itemView.findViewById(R.id.decline_button);
            proposeButton = itemView.findViewById(R.id.propose_button);
        }
    }
}