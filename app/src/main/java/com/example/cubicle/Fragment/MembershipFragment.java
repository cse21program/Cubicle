package com.example.cubicle.Fragment;

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

import com.example.cubicle.Model.DetailOfClubs;
import com.example.cubicle.PostAndChatActivity;
import com.example.cubicle.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


public class MembershipFragment extends Fragment {
    private RecyclerView MembershipRecyclerview;
    private DatabaseReference MembersRef,Club_DetailsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_membership, container, false);
        MembershipRecyclerview=view.findViewById(R.id.membership_recyclerview);
        MembershipRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        MembersRef=FirebaseDatabase.getInstance().getReference().child("Club Members");
        Club_DetailsRef=FirebaseDatabase.getInstance().getReference().child("Club Details");

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();


       return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<DetailOfClubs> options=new FirebaseRecyclerOptions.Builder<DetailOfClubs>()
                .setQuery(MembersRef.child(currentUserID),DetailOfClubs.class)
                .build();
        FirebaseRecyclerAdapter<DetailOfClubs,MembershipViewHolder> adapter=new FirebaseRecyclerAdapter<DetailOfClubs, MembershipViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MembershipViewHolder holder, int position, @NonNull DetailOfClubs detailOfClubs) {
                final String visit_club_id=getRef(position).getKey();
                Club_DetailsRef.child(visit_club_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                           String name=dataSnapshot.child("name").getValue().toString();
                            holder.Club_Profile_Name.setText(name);
                        }

                        if (dataSnapshot.hasChild("profile_picture")){
                            final String profile_picture=dataSnapshot.child("profile_picture").getValue().toString();

                            Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Club_Profile_Image, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(profile_picture).into(holder.Club_Profile_Image);
                                }
                            });
                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getContext(), PostAndChatActivity.class);
                                intent.putExtra("visit_club_id",visit_club_id);
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
            public MembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_profile_sample,parent,false);
                MembershipViewHolder viewHolder=new MembershipViewHolder(view);
                return viewHolder;
            }
        };
        MembershipRecyclerview.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MembershipViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView Club_Profile_Image;
        private TextView Club_Profile_Name;

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);
            Club_Profile_Image=itemView.findViewById(R.id.imageViewOfClub);
            Club_Profile_Name=itemView.findViewById(R.id.club_profile_name);
        }
    }
}
