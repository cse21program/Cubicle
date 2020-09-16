package com.example.cubicle;

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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cubicle.Adapter.MessageAdapter;
import com.example.cubicle.Model.Message;
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
    private String visit_club_id;
    private DatabaseReference ClubDetailsRef,MessageRef;
    private Toolbar MessageToolbar;
    private CircleImageView toolbarProfile;
    private TextView toolbarName;
    private EditText MessageInput;
    private ImageButton MessageSendBtn;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private RecyclerView MessageRecyclerView;
    private ScrollView mScrollView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        MessageToolbar=findViewById(R.id.messageToolBar);
        toolbarProfile=findViewById(R.id.toolbarProfile);
        toolbarName=findViewById(R.id.tool_barName);
        MessageInput=findViewById(R.id.MessageInputET);
        MessageSendBtn=findViewById(R.id.sendMessageBtn);
        messageAdapter=new MessageAdapter(messageList);
        MessageRecyclerView=findViewById(R.id.messageRecyclerview);
        mScrollView=findViewById(R.id.scroll);
        MessageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        MessageRecyclerView.setAdapter(messageAdapter);

        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();
        MessageRef=FirebaseDatabase.getInstance().getReference().child("Message");

        setSupportActionBar(MessageToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MessageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MessageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageUpdate();
            }
        });


        ClubDetailsRef= FirebaseDatabase.getInstance().getReference().child("Club Details");
        visit_club_id=getIntent().getExtras().get("visit").toString();

        getClubInfo();
        getRetriver();

    }

    private void getRetriver() {
        MessageRef.child(CurrentUserID).child(visit_club_id).addChildEventListener(new ChildEventListener() {
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

    private void MessageUpdate() {
        String message=MessageInput.getText().toString();
        if (message.isEmpty())
        {
            Toast.makeText(this, "Write message..", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat CurrentDate=new SimpleDateFormat("dd-MMMM-yyyy");
            String saveCurrentDate=CurrentDate.format(calendar.getTime());

            Calendar calendar1=Calendar.getInstance();
            SimpleDateFormat CurrentTime=new SimpleDateFormat("hh:mm a");
            String saveCurrentTime=CurrentTime.format(calendar1.getTime());
            final String postkey=MessageRef.push().getKey();
            final HashMap<String,Object> messageMap=new HashMap<>();
            messageMap.put("message",message);
            messageMap.put("to",visit_club_id);
            messageMap.put("from",CurrentUserID);
            messageMap.put("date",saveCurrentDate);
            messageMap.put("time",saveCurrentTime);
            messageMap.put("type","text");

            MessageRef.child(CurrentUserID).child(visit_club_id).child(postkey).updateChildren(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   MessageRef.child(visit_club_id).child(CurrentUserID).child(postkey).updateChildren(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful())
                         {
                             Toast.makeText(MessageActivity.this, "Message send successfully", Toast.LENGTH_SHORT).show();

                         }
                       }
                   });
                }
            });
            MessageInput.setText("");
        }
    }

    private void getClubInfo() {
        ClubDetailsRef.child(visit_club_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();

                toolbarName.setText(name);
                if ((dataSnapshot.hasChild("profile_picture"))) {
                    final String profile = dataSnapshot.child("profile_picture").getValue().toString();

                    Picasso.get().load(profile).networkPolicy(NetworkPolicy.OFFLINE).into(toolbarProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile).into(toolbarProfile);
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
