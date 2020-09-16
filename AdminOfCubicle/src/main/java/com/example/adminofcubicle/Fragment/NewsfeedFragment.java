package com.example.adminofcubicle.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.adminofcubicle.Adapter.PostAdapter;
import com.example.adminofcubicle.Model.PostItem;
import com.example.adminofcubicle.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsfeedFragment extends Fragment {
    private RecyclerView NewsFeedRecyclerView;
    private DatabaseReference ClubPostRef,ClubDetailsRef;
    private List<PostItem> itemList;
    private PostAdapter adapter;
    private String key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_newsfeed, container, false);
        itemList=new ArrayList<>();
        adapter=new PostAdapter(getActivity(),itemList);
        itemList.clear();

        NewsFeedRecyclerView=view.findViewById(R.id.NewsFeedRecyclerview);
        NewsFeedRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        NewsFeedRecyclerView.setLayoutManager(layoutManager);
        NewsFeedRecyclerView.setAdapter(adapter);

        ClubPostRef=FirebaseDatabase.getInstance().getReference().child("Club Posts");
        ClubDetailsRef=FirebaseDatabase.getInstance().getReference().child("Club Details");

        Post();

        return view;
    }

    private void Post() {
        ClubDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String key=dataSnapshot1.getKey();
                    fun(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fun(String key) {
        ClubPostRef.child(key).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
             PostItem item=dataSnapshot.getValue(PostItem.class);
             itemList.add(item);
             adapter.notifyDataSetChanged();
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

}
