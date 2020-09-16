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
import android.widget.TextView;
import com.example.adminofcubicle.Model.StudentDetails;
import com.example.adminofcubicle.R;
import com.example.adminofcubicle.StudentProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class MembersFragment extends Fragment {
    private RecyclerView Members_recyclerview;
    private FirebaseAuth  mAuth;
    private String currentUserID;
    private DatabaseReference StudentRef,MemberRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members, container, false);
        Members_recyclerview = view.findViewById(R.id.members_recyclerview);
        Members_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        StudentRef = FirebaseDatabase.getInstance().getReference().child("Student Details");
        MemberRef = FirebaseDatabase.getInstance().getReference().child("Club Members");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<StudentDetails> options=new FirebaseRecyclerOptions.Builder<StudentDetails>()
                .setQuery(MemberRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()),StudentDetails.class)
                .build();
        FirebaseRecyclerAdapter<StudentDetails,MembersViewHolder> adapter=new FirebaseRecyclerAdapter<StudentDetails, MembersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MembersViewHolder holder, int position, @NonNull StudentDetails studentDetails) {

                final String visit_Student_id=getRef(position).getKey();
                StudentRef.child(visit_Student_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("image")){
                           final String retrieveImage=dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(retrieveImage).into(holder.profile_image);
                        }

                        final String studentName=dataSnapshot.child("name").getValue().toString();
                        final String studentBatch=dataSnapshot.child("batch").getValue().toString();
                        final String studentDepartment=dataSnapshot.child("department").getValue().toString();
                        final String studentUniversity=dataSnapshot.child("university").getValue().toString();

                        holder.Student_name.setText(studentName);
                        holder.Student_Details.setText(studentBatch+"\n"+studentDepartment+"\n"+studentUniversity);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getContext(), StudentProfileActivity.class);
                                intent.putExtra("visit_student_id",visit_Student_id);
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
                MembersViewHolder viewHolder=new MembersViewHolder(view);
                return viewHolder;
            }
        };
        Members_recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    private static class MembersViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profile_image;
        private TextView Student_name,Student_Details;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.student_image);
            Student_name=itemView.findViewById(R.id.student_name);
            Student_Details=itemView.findViewById(R.id.student_details);
        }
    }

}