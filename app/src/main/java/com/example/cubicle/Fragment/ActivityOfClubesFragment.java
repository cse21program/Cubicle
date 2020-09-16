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

import com.example.cubicle.ClubProfileActivity;
import com.example.cubicle.Model.DetailOfClubs;
import com.example.cubicle.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ActivityOfClubesFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference RootRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_activity_of_clubes, container, false);

        RootRef= FirebaseDatabase.getInstance().getReference().child("Club Details");
        recyclerView=view.findViewById(R.id.activity_of_club_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<DetailOfClubs> option=new FirebaseRecyclerOptions.Builder<DetailOfClubs>()
                .setQuery(RootRef,DetailOfClubs.class)
                .build();

        FirebaseRecyclerAdapter<DetailOfClubs,DetailOfClubsViewHolder> adapter=new FirebaseRecyclerAdapter<DetailOfClubs, DetailOfClubsViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final DetailOfClubsViewHolder holder, final int position, @NonNull final DetailOfClubs model) {
                final String visit_clubs_id=getRef(position).getKey();
                RootRef.child(visit_clubs_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String name=dataSnapshot.child("name").getValue().toString();

                            holder.Profile_Name.setText(name);


                        }

                        if (dataSnapshot.hasChild("profile_picture")){
                            final String profile_picture=dataSnapshot.child("profile_picture").getValue().toString();

                            Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Profile_Picture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(profile_picture).into(holder.Profile_Picture);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_clubs_id=getRef(position).getKey();
                        Intent intent=new Intent(getContext(), ClubProfileActivity.class);
                        intent.putExtra("visit_clubs_id",visit_clubs_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public DetailOfClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View view=LayoutInflater.from(getContext()).inflate(R.layout.clubs_profile_sample,parent,false);
                 DetailOfClubsViewHolder viewHolder=new DetailOfClubsViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class DetailOfClubsViewHolder extends RecyclerView.ViewHolder{
        TextView Profile_Name;
        CircleImageView Profile_Picture;

        public DetailOfClubsViewHolder(@NonNull View itemView) {
            super(itemView);

            Profile_Name=itemView.findViewById(R.id.club_profile_name);
            Profile_Picture=itemView.findViewById(R.id.imageViewOfClub);

        }
    }
}
