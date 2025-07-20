package com.example.irr_project;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Import lớp Application đã được tách riêng
// import com.example.irr_project.model.Application; // Nếu bạn đặt Application trong package model
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private final List<Application> applications; // Sử dụng lớp Application đã import
    private Context context; // Giữ lại nếu cần cho các thao tác đặc thù context trong adapter
    private final OnApplicationActionListener listener;

    // Interface sử dụng lớp Application đã import
    public interface OnApplicationActionListener {
        void onWithdrawClick(Application application, int position);
        void onScheduleInterviewClick(Application application, int position);
    }

    public ApplicationAdapter(Context context, List<Application> applications, OnApplicationActionListener listener) {
        this.context = context; // context có thể hữu ích cho việc inflate layout hoặc lấy string resources
        this.applications = applications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applications.get(position);
        holder.bind(application, listener, position);
    }

    @Override
    public int getItemCount() {
        return applications == null ? 0 : applications.size();
    }

    public void updateApplications(List<Application> newApplications) {
        // Cân nhắc sử dụng DiffUtil để cải thiện hiệu năng nếu danh sách lớn
        this.applications.clear();
        if (newApplications != null) {
            this.applications.addAll(newApplications);
        }
        notifyDataSetChanged();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView tvInternshipTitle, tvCompanyName, tvStatus;
        Button btnWithdraw, btnScheduleInterview;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInternshipTitle = itemView.findViewById(R.id.tvApplicationInternshipTitle);
            tvCompanyName = itemView.findViewById(R.id.tvApplicationCompanyName);
            tvStatus = itemView.findViewById(R.id.tvApplicationStatus);
            btnWithdraw = itemView.findViewById(R.id.btnWithdrawApplication);
            btnScheduleInterview = itemView.findViewById(R.id.btnScheduleInterview);
        }

        // bind sử dụng lớp Application đã import
        public void bind(final Application application, final OnApplicationActionListener listener, final int position) {
            tvInternshipTitle.setText(application.getInternshipTitle());
            tvCompanyName.setText(application.getCompanyName());
            tvStatus.setText("Trạng thái: " + application.getStatus());

            // Thiết lập màu sắc và visibility cho button dựa trên trạng thái
            // Sử dụng Application.Status từ lớp Application đã tách riêng
            switch (application.getStatus()) {
                case Application.Status.ACCEPTED:
                    tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                    btnWithdraw.setVisibility(View.GONE);
                    btnScheduleInterview.setVisibility(View.VISIBLE);
                    break;
                case Application.Status.REJECTED:
                    tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
                    btnWithdraw.setVisibility(View.GONE);
                    btnScheduleInterview.setVisibility(View.GONE);
                    break;
                case Application.Status.PENDING:
                    tvStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
                    btnWithdraw.setVisibility(View.VISIBLE);
                    btnScheduleInterview.setVisibility(View.GONE);
                    break;
                case Application.Status.UNDER_REVIEW:
                    tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                    btnWithdraw.setVisibility(View.VISIBLE);
                    btnScheduleInterview.setVisibility(View.GONE);
                    break;
                case Application.Status.WITHDRAWN:
                    tvStatus.setTextColor(Color.DKGRAY);
                    btnWithdraw.setVisibility(View.GONE);
                    btnScheduleInterview.setVisibility(View.GONE);
                    break;
                default:
                    tvStatus.setTextColor(Color.BLACK); // Hoặc màu mặc định
                    btnWithdraw.setVisibility(View.GONE);
                    btnScheduleInterview.setVisibility(View.GONE);
                    break;
            }

            btnWithdraw.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWithdrawClick(application, position);
                }
            });

            btnScheduleInterview.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onScheduleInterviewClick(application, position);
                }
            });
        }
    }
}
