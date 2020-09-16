package com.example.cubicle.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cubicle.Adapter.PostAdapter;
import com.example.cubicle.Model.PostItem;
import com.example.cubicle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment {
    private RecyclerView NewsFeedRecyclerview;
    private DatabaseReference ClubPostsRef,ClubMembersRef;
    private List<PostItem> itemList;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private PostAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_news_feed, container, false);

        itemList=new ArrayList<>();
        itemList.clear();
        adapter=new PostAdapter(getContext(),itemList);

        NewsFeedRecyclerview=view.findViewById(R.id.newsfeed_RecyclerView);
        NewsFeedRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        NewsFeedRecyclerview.setLayoutManager(layoutManager);
        NewsFeedRecyclerview.setAdapter(adapter);




       ClubPostsRef=FirebaseDatabase.getInstance().getReference().child("Club Posts");
       ClubMembersRef=FirebaseDatabase.getInstance().getReference().child("Club Members");



       mAuth=FirebaseAuth.getInstance();
       CurrentUserID=mAuth.getCurrentUser().getUid();
        Post();


       return view;
    }


    public void Post(){
        ClubMembersRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren())
                {
                    String key=child.getKey();
                    fun(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fun(String key) {
        ClubPostsRef.child(key).addChildEventListener(new ChildEventListener() {
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
