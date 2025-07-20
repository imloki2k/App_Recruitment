package com.example.irr_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.irr_project.adapter.MessageAdapter;
import com.example.irr_project.model.Message;
import com.example.irr_project.utils.UserSession;
import com.example.irr_project.database.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecruiterChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private UserSession userSession;
    private DatabaseHelper dbHelper;
    private String studentId;
    private String chatId;
    private DatabaseReference databaseReference;
    private TextView title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_chat);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        userSession = new UserSession(this);
        dbHelper = new DatabaseHelper(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        studentId = getIntent().getStringExtra("studentId");
        String recruiterEmail = userSession.getUserEmail();
        String studentEmail = getStudentEmail(studentId);
        chatId = createChatId(studentEmail, recruiterEmail);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        messageAdapter.setCurrentUserId(recruiterEmail);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        listenForMessages();

        // Xử lý nút back
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (title != null) {
            title.setText("Nhắn với " + studentEmail);
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;
        String senderId = userSession.getUserEmail();
        Message recruiterMsg = new Message(content, senderId, System.currentTimeMillis());
        DatabaseReference chatRef = databaseReference.child("chats").child(chatId).child("messages");
        String messageId = chatRef.push().getKey();
        if (messageId != null) {
            chatRef.child(messageId).setValue(recruiterMsg)
                .addOnSuccessListener(aVoid -> Log.d("RecruiterChatActivity", "Message sent successfully"))
                .addOnFailureListener(e -> Log.e("RecruiterChatActivity", "Failed to send message", e));
        }
        editTextMessage.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
        }
    }

    private void listenForMessages() {
        DatabaseReference chatRef = databaseReference.child("chats").child(chatId).child("messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                messageList.clear();
                messageList.addAll(messages);
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("RecruiterChatActivity", "Failed to read messages from Firebase", error.toException());
            }
        });
    }

    private String getStudentEmail(String studentId) {
        try {
            int id = Integer.parseInt(studentId);
            String email = dbHelper.getStudentEmailById(id);
            if (email != null) {
                return email;
            }
        } catch (NumberFormatException e) {
            return studentId;
        }
        return "student" + studentId + "@example.com";
    }

    private String createChatId(String email1, String email2) {
        String safeEmail1 = email1.replaceAll("[.#$\\[\\]]", "_");
        String safeEmail2 = email2.replaceAll("[.#$\\[\\]]", "_");
        if (safeEmail1.compareTo(safeEmail2) < 0) {
            return safeEmail1 + "_" + safeEmail2;
        } else {
            return safeEmail2 + "_" + safeEmail1;
        }
    }
} 