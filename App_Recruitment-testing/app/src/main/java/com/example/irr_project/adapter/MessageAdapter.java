package com.example.irr_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.irr_project.model.Message;
import com.example.irr_project.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messageList;
    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECEIVER = 1;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void setCurrentUserId(String id) { this.currentUserId = id; }

    @Override
    public int getItemViewType(int position) {
        Message msg = messageList.get(position);
        return msg.getSenderId() != null && msg.getSenderId().equals(currentUserId) ? TYPE_SENDER : TYPE_RECEIVER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).textViewMessage.setText(message.getContent());
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).textViewMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        SenderViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        ReceiverViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
} 