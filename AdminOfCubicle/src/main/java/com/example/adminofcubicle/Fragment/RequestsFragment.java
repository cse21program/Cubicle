package com.example.adminofcubicle.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.Model.StudentDetails;
import com.example.adminofcubicle.R;
import com.example.adminofcubicle.StudentProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsFragment extends Fragment {
    private RecyclerView request_recyclerView;
    private DatabaseReference MembershipRef,StudentsRef,MemberRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_requests, container, false);
      request_recyclerView= view.findViewById(R.id.member_request);

      MembershipRef= FirebaseDatabase.getInstance().getReference().child("Membership Request");
      StudentsRef= FirebaseDatabase.getInstance().getReference().child("Student Details");
      MemberRef=FirebaseDatabase.getInstance().getReference().child("Club Members");
      mAuth=FirebaseAuth.getInstance();
      currentUserID=mAuth.getCurrentUser().getUid();

      request_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<StudentDetails> options=new FirebaseRecyclerOptions.Builder<StudentDetails>()
                .setQuery(MembershipRef.child(currentUserID),StudentDetails.class)
                .build();

        FirebaseRecyclerAdapter<StudentDetails,RequestViewHolder> adapter=new FirebaseRecyclerAdapter<StudentDetails, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull StudentDetails model) {
                final  String visit_student_details_id=getRef(position).getKey();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String type=dataSnapshot.getValue().toString();
                            if (type.equals("received")){
                                StudentsRef.child(visit_student_details_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")){
                                            final String retrieveImage=dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(retrieveImage).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Profile_Image, new Callback() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Picasso.get().load(retrieveImage).into(holder.Profile_Image);
                                                }
                                            });
                                        }
                                        final String studentName=dataSnapshot.child("name").getValue().toString();
                                        holder.Profile_Name.setText(studentName);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent=new Intent(getContext(), StudentProfileActivity.class);
                                                intent.putExtra("visit_student_id",visit_student_details_id);

                                                startActivity(intent);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

             holder.Request_Accept_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MemberRef.child(currentUserID).child(visit_student_details_id).child("Club Members").setValue("Saved")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                MemberRef.child(visit_student_details_id).child(currentUserID).child("Club Members").setValue("Saved")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    MembershipRef.child(currentUserID).child(visit_student_details_id)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                MembershipRef.child(visit_student_details_id).child(currentUserID)
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            Toast.makeText(getContext(), "Accepted a new member..", Toast.LENGTH_SHORT).show();
                                                                                        }

                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            }
                                    }
                                });

                    }
                });

            holder.Request_Cancel_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MembershipRef.child(currentUserID).child(visit_student_details_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                MembershipRef.child(visit_student_details_id).child(currentUserID)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getContext(), "Deleted a request..", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                            }
                        }
                    });

                }
            });


            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item2,parent,false);
                RequestViewHolder viewHolder=new RequestViewHolder(view);
                return viewHolder;
            }
        };
        request_recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView Profile_Image;
        private TextView Profile_Name;
        private Button Request_Accept_Btn,Request_Cancel_Btn;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            Profile_Image=itemView.findViewById(R.id.student_profile_image);
            Profile_Name=itemView.findViewById(R.id.student_profile_name);
            Request_Accept_Btn=itemView.findViewById(R.id.student_accept_btn);
            Request_Cancel_Btn=itemView.findViewById(R.id.student_cancel_btn);
        }
    }


}
