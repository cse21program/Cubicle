package com.example.adminofcubicle.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminofcubicle.MessageActivity;
import com.example.adminofcubicle.Model.Message;
import com.example.adminofcubicle.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.coumstom_message_layout,parent,false);
        MessageViewHolder viewHolder=new MessageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String CurrentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Message message=messageList.get(position);
        String fromMessageType=message.getType();
        String fromMessage=message.getFrom();

        holder.SenderMessageTimeAndDate.setVisibility(View.GONE);
        holder.SenderMessage.setVisibility(View.GONE);
        holder.ReceiverMessageTimeAndDate.setVisibility(View.GONE);
        holder.ReceiverMessage.setVisibility(View.GONE);
        if (fromMessageType.equals("text"))
        {
            if (fromMessage.equals(CurrentUserID))
            {
                holder.SenderMessageTimeAndDate.setVisibility(View.VISIBLE);
                holder.SenderMessage.setVisibility(View.VISIBLE);
                holder.SenderMessage.setText(message.getMessage());
                holder.SenderMessageTimeAndDate.setText(message.getTime()+"\t"+message.getDate());

            }
            else {
                holder.ReceiverMessage.setVisibility(View.VISIBLE);
                holder.ReceiverMessageTimeAndDate.setVisibility(View.VISIBLE);
                holder.ReceiverMessage.setText(message.getMessage());
                holder.ReceiverMessageTimeAndDate.setText(message.getTime()+"\t"+message.getDate());

            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView ReceiverMessage,ReceiverMessageTimeAndDate,SenderMessage,SenderMessageTimeAndDate;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ReceiverMessage=itemView.findViewById(R.id.receiverMessage);
            ReceiverMessageTimeAndDate=itemView.findViewById(R.id.receiverMessageTimeAndDate);
            SenderMessage=itemView.findViewById(R.id.SenderMessage);
            SenderMessageTimeAndDate=itemView.findViewById(R.id.SenderMessageTimeAndDate);
        }
    }
}
