package com.example.adminofcubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClubsProfileActivity extends AppCompatActivity {
    private Toolbar ClubsProfileToolbar;
    private String ClubPostVisitor;
    private DatabaseReference ClubRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs_profile);

        ClubRef= FirebaseDatabase.getInstance().getReference().child("Club Details");

        ClubsProfileToolbar=findViewById(R.id.clubsProfileToolbar);
        setSupportActionBar(ClubsProfileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

       ClubPostVisitor= getIntent().getExtras().get("key").toString();

        ClubsProfileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        RetrieveClubInfo();
    }

    private void RetrieveClubInfo() {
        ClubRef.child(ClubPostVisitor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                getSupportActionBar().setTitle(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
