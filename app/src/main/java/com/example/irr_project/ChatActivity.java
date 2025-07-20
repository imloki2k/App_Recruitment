package com.example.irr_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private ChatAdapter chatAdapter;
    private EditText editTextMessage;
    private Button buttonSend;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private int userId = -1;
    private static final String RECRUITER_ID = "recruiter1"; // Giả lập recruiter ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize UI components
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        findViewById(R.id.buttonLogout).setOnClickListener(v -> logout());

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) {
            finish(); // Quay lại nếu không đăng nhập
            return;
        }

        // Set up RecyclerView
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setAdapter(chatAdapter);

        // Thêm tin nhắn mẫu từ recruiter
        chatMessages.add(new ChatMessage(RECRUITER_ID, "Chào bạn! Bạn cần hỗ trợ gì?", "11:00 PM"));
        chatAdapter.notifyDataSetChanged();

        // Set up send button
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            // Thêm tin nhắn của sinh viên
            String timestamp = "11:13 PM"; // Dựa trên thời gian hiện tại
            chatMessages.add(new ChatMessage(String.valueOf(userId), message, timestamp));
            editTextMessage.setText("");

            // Mô phỏng phản hồi từ recruiter
            simulateRecruiterResponse(message);
            recyclerViewChat.scrollToPosition(chatMessages.size() - 1); // Cuộn xuống tin nhắn mới
        }
    }

    private void simulateRecruiterResponse(String studentMessage) {
        String[] predefinedReplies = {
                "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ xem xét và phản hồi sớm.",
                "Bạn có thể gửi CV qua email nhé!",
                "Hiện tại chưa có vị trí phù hợp, hãy thử lại sau."
        };
        int randomIndex = (int) (Math.random() * predefinedReplies.length);
        String recruiterReply = predefinedReplies[randomIndex];
        chatMessages.add(new ChatMessage(RECRUITER_ID, recruiterReply, "11:14 PM")); // Dựa trên thời gian hiện tại
        chatAdapter.notifyDataSetChanged();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Xóa tất cả dữ liệu đăng nhập
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

// Model cho tin nhắn
class ChatMessage {
    String senderId;
    String message;
    String timestamp;

    ChatMessage(String senderId, String message, String timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }
}

// Adapter cho RecyclerView
class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;
    private Context context;

    ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.textViewMessage.setText(message.message);
        holder.textViewTimestamp.setText(message.timestamp);

        // Điều chỉnh vị trí và background dựa trên senderId
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.itemView.getLayoutParams();
        if ("recruiter1".equals(message.senderId)) {
            holder.textViewMessage.setBackgroundResource(R.drawable.bg_recruiter_message);
            params.startToStart = ConstraintLayout.LayoutParams.UNSET; // Xóa ràng buộc start
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // Căn phải
            holder.itemView.setLayoutParams(params);
        } else {
            holder.textViewMessage.setBackgroundResource(R.drawable.bg_student_message);
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET; // Xóa ràng buộc end
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // Căn trái
            holder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTimestamp;

        ChatViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
        }
    }
}