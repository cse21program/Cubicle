package com.example.adminofcubicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.Adapter.MessageAdapter;
import com.example.adminofcubicle.Model.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private String Student_visit_id;
    private CircleImageView ToolBarProfileImage;
    private TextView ToolBarName;
    private Toolbar MessageToolBar;
    private DatabaseReference StudentRef,MessageRef;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private ImageButton MessageSendBtn;
    private EditText MessageInputET;
    private RecyclerView MessageRecyclerView;
    private List<Message> messageList=new ArrayList<>();
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ToolBarProfileImage=findViewById(R.id.tool_barProfile);
        ToolBarName=findViewById(R.id.tool_barName);
        MessageToolBar=findViewById(R.id.messageToolBar);
        MessageSendBtn=findViewById(R.id.MessageSentBtn);
        MessageInputET=findViewById(R.id.messageInputET);
        MessageRecyclerView=findViewById(R.id.MessageRecyclerView);
        messageAdapter=new MessageAdapter(messageList);
        MessageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        StudentRef= FirebaseDatabase.getInstance().getReference().child("Student Details");
        MessageRef=FirebaseDatabase.getInstance().getReference().child("Message");
        MessageRecyclerView.setAdapter(messageAdapter);
        setSupportActionBar(MessageToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MessageToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();


        Student_visit_id=getIntent().getExtras().get("visit_student_id").toString();
       getStudentInfo();
       MessageSendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               MessageSendMethod();
           }
       });
       getRetriver();

    }

    private void getRetriver() {
        MessageRef.child(CurrentUserID).child(Student_visit_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message=dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                MessageRecyclerView.smoothScrollToPosition(MessageRecyclerView.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MessageSendMethod() {
        String message=MessageInputET.getText().toString();
        final String messagePushKey=MessageRef.push().getKey();
        if (message.isEmpty())
        {
            Toast.makeText(this, "Write message", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat ForDate=new SimpleDateFormat("dd-MMMM-yyyy");
            String CurrentDate=ForDate.format(calendar.getTime());

            Calendar calendar1=Calendar.getInstance();
            SimpleDateFormat ForTime=new SimpleDateFormat("hh:mm a");
            String CurrentTime=ForTime.format(calendar1.getTime());

            final HashMap<String,Object> messageMap=new HashMap<>();
            messageMap.put("message",message);
            messageMap.put("time",CurrentTime);
            messageMap.put("date",CurrentDate);
            messageMap.put("to",Student_visit_id);
            messageMap.put("from",CurrentUserID);
            messageMap.put("type","text");

            MessageRef.child(Student_visit_id).child(CurrentUserID).child(messagePushKey).updateChildren(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   MessageRef.child(CurrentUserID).child(Student_visit_id).child(messagePushKey).updateChildren(messageMap)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful())
                                   {
                                       Toast.makeText(MessageActivity.this, "Message Sent successfully", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                }
            });
            MessageInputET.setText("");
        }
    }

    private void getStudentInfo() {
        StudentRef.child(Student_visit_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                ToolBarName.setText(name);
                if(dataSnapshot.hasChild("image"))
                {
                    final String image=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ToolBarProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                        Picasso.get().load(image).into(ToolBarProfileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
