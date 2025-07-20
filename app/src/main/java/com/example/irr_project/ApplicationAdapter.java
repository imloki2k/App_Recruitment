package com.example.irr_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private Context context;
    private List<Application> applications;
    private OnApplicationActionListener listener;
    private String userRole;

    public interface OnApplicationActionListener {
        void onWithdrawClick(Application application, int position);
        void onScheduleInterviewClick(Application application, int position);
        void onStatusChange(Application application, int position, String newStatus);
    }

    public ApplicationAdapter(Context context, List<Application> applications, OnApplicationActionListener listener, String userRole) {
        this.context = context;
        this.applications = applications;
        this.listener = listener;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Application app = applications.get(position);
        holder.internshipTitleTextView.setText(app.getInternshipTitle());
        holder.companyNameTextView.setText("Công ty: " + app.getCompanyName());
        holder.statusTextView.setText("Trạng thái: " + app.getStatus());
        holder.studentIdTextView.setText("Student ID: " + app.getStudentId()); // Hiển thị student_id

        // Hiển thị/ẩn nút và spinner dựa trên vai trò và trạng thái
        if (userRole.equals("student")) {
            boolean canWithdraw = app.getStatus().equals(Application.Status.PENDING.toString()) ||
                    app.getStatus().equals(Application.Status.UNDER_REVIEW.toString()) ||
                    app.getStatus().equals(Application.Status.ACCEPTED.toString());
            holder.withdrawButton.setVisibility(canWithdraw ? View.VISIBLE : View.GONE);
            holder.scheduleInterviewButton.setVisibility(
                    app.getStatus().equals(Application.Status.ACCEPTED.toString()) ? View.VISIBLE : View.GONE
            );
            holder.statusSpinner.setVisibility(View.GONE);
        } else if (userRole.equals("recruiter")) {
            holder.withdrawButton.setVisibility(View.GONE);
            holder.scheduleInterviewButton.setVisibility(
                    app.getStatus().equals(Application.Status.ACCEPTED.toString()) ? View.VISIBLE : View.GONE
            );
            holder.statusSpinner.setVisibility(View.VISIBLE);

            // Thiết lập Spinner với danh sách trạng thái
            List<String> statusList = Arrays.asList(
                    Application.Status.PENDING.toString(),
                    Application.Status.UNDER_REVIEW.toString(),
                    Application.Status.ACCEPTED.toString(),
                    Application.Status.REJECTED.toString(),
                    Application.Status.WITHDRAWN.toString()
            );
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.statusSpinner.setAdapter(adapter);

            // Đặt trạng thái hiện tại
            int spinnerPosition = statusList.indexOf(app.getStatus());
            holder.statusSpinner.setSelection(spinnerPosition);

            // Xử lý thay đổi trạng thái
            holder.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String newStatus = statusList.get(pos);
                    if (!newStatus.equals(app.getStatus())) {
                        listener.onStatusChange(app, position, newStatus);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Không làm gì
                }
            });
        } else {
            holder.withdrawButton.setVisibility(View.GONE);
            holder.scheduleInterviewButton.setVisibility(View.GONE);
            holder.statusSpinner.setVisibility(View.GONE);
        }

        holder.withdrawButton.setOnClickListener(v -> listener.onWithdrawClick(app, position));
        holder.scheduleInterviewButton.setOnClickListener(v -> listener.onScheduleInterviewClick(app, position));
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView internshipTitleTextView;
        TextView companyNameTextView;
        TextView statusTextView;
        TextView studentIdTextView; // Thêm TextView cho student_id
        Button withdrawButton;
        Button scheduleInterviewButton;
        Spinner statusSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            internshipTitleTextView = itemView.findViewById(R.id.internship_title_textview);
            companyNameTextView = itemView.findViewById(R.id.company_name_textview);
            statusTextView = itemView.findViewById(R.id.status_textview);
            studentIdTextView = itemView.findViewById(R.id.tvStudentId); // Liên kết với ID trong layout
            withdrawButton = itemView.findViewById(R.id.withdraw_button);
            scheduleInterviewButton = itemView.findViewById(R.id.schedule_interview_button);
            statusSpinner = itemView.findViewById(R.id.status_spinner);
        }
    }
}