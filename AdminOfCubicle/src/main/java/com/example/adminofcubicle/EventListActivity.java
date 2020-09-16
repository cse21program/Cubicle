package com.example.adminofcubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.Model.EventMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventListActivity extends AppCompatActivity {
    private String parent_uid,child_uid,event_name;
    private DatabaseReference PostRef;
    private RecyclerView EventListRecyclerView;
    private TextView Event_Caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Event_Caption=findViewById(R.id.eventCaption);
        EventListRecyclerView=findViewById(R.id.eventRegisterRecycler);
        EventListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        parent_uid=getIntent().getExtras().get("parent_uid").toString();
        child_uid=getIntent().getExtras().get("child_uid").toString();
        event_name=getIntent().getExtras().get("event_name").toString();
        Event_Caption.setText(event_name+" Register People List");

        PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts").child(parent_uid).child(child_uid).child("Register Members");



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<EventMember> options=new FirebaseRecyclerOptions.Builder<EventMember>()
                .setQuery(PostRef,EventMember.class)
                .build();

        FirebaseRecyclerAdapter<EventMember,EventListViewHolder> adapter=new FirebaseRecyclerAdapter<EventMember, EventListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventListViewHolder holder, int position, @NonNull final EventMember model) {
                final String string=getRef(position).getKey();

                if (model.getReg_type().equals("Approved"))
                {
                    holder.Profile_Name.setText(model.getName());
                    holder.Request_Cancel_Btn.setVisibility(View.GONE);
                    holder.Request_Accept_Btn.setVisibility(View.GONE);
                    holder.Details.setText("StudentId:"+model.getStudentId()+"\nPhone:"+model.getPhone()+"\nEmail:"+model.getEmail());


                    Picasso.get().load(model.getProfile_picture()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Profile_Image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(model.getProfile_picture()).into(holder.Profile_Image);
                        }
                    });

                }
                else {
                    holder.Profile_Name.setText(model.getName());
                    holder.Details.setText("Wants to join with "+event_name+" event");

                    Picasso.get().load(model.getProfile_picture()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Profile_Image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(model.getProfile_picture()).into(holder.Profile_Image);
                        }
                    });
                    holder.Request_Accept_Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("reg_type","Approved");
                            PostRef.child(string).updateChildren(hashMap);


                        }
                    });

                    holder.Request_Cancel_Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostRef.child(string).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(EventListActivity.this, "Request Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }

            }

            @NonNull
            @Override
            public EventListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item2,parent,false);
                EventListViewHolder viewHolder=new EventListViewHolder(view);
                return viewHolder;
            }
        };
        EventListRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    class EventListViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView Profile_Image;
        private TextView Profile_Name,Details;
        private Button Request_Accept_Btn,Request_Cancel_Btn;

        public EventListViewHolder(@NonNull View itemView) {
            super(itemView);
            Profile_Image=itemView.findViewById(R.id.student_profile_image);
            Profile_Name=itemView.findViewById(R.id.student_profile_name);
            Request_Accept_Btn=itemView.findViewById(R.id.student_accept_btn);
            Request_Cancel_Btn=itemView.findViewById(R.id.student_cancel_btn);
            Details=itemView.findViewById(R.id.details);
        }
    }
}
