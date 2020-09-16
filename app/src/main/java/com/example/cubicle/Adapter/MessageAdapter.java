package com.example.cubicle.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cubicle.MessageActivity;
import com.example.cubicle.Model.Message;
import com.example.cubicle.R;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.coustom_message_layout,parent,false);
        MessageViewHolder viewHolder=new MessageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String CurrentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Message message=messageList.get(position);
        String fromMessageType=message.getType();
        String  fromUserID=message.getFrom();
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverMessageTimeAndDate.setVisibility(View.GONE);
        holder.senderMessageTimeAndDate.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);

        if (fromMessageType.equals("text")){

            if (fromUserID.equals(CurrentUserID)){

                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageTimeAndDate.setVisibility(View.VISIBLE);
                holder.senderMessageText.setText(message.getMessage());
                holder.senderMessageTimeAndDate.setText(message.getTime()+"\t"+message.getDate());

            }else {

                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageTimeAndDate.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(message.getMessage());
                holder.receiverMessageTimeAndDate.setText(message.getTime()+"\t"+message.getDate());



            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText,receiverMessageText,senderMessageTimeAndDate,receiverMessageTimeAndDate;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText=itemView.findViewById(R.id.senderMessage);
            receiverMessageText=itemView.findViewById(R.id.receiverMessage);
            senderMessageTimeAndDate=itemView.findViewById(R.id.senderMessageTimeAndTime);
            receiverMessageTimeAndDate=itemView.findViewById(R.id.receiverMessageTimeAndTime);
        }
    }
}
