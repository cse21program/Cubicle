package com.example.adminofcubicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventsMangeActivity extends AppCompatActivity {
    private EditText Event_Name,Event_Description;
    private Button CreateEventBtn;
    private DatabaseReference PostKeyRef,PostRef,PostRef2,ClubRef;
    private FirebaseAuth mAuth;
    private long count=0;
    private String CurrentClubID,CurrentClubName,CurrentClubProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_mange);

        Event_Name=findViewById(R.id.event_name);
        Event_Description=findViewById(R.id.event_description);
        CreateEventBtn=findViewById(R.id.eventCreateBtn);
        mAuth=FirebaseAuth.getInstance();
        CurrentClubID=mAuth.getCurrentUser().getUid();
        PostRef=FirebaseDatabase.getInstance().getReference().child("Club Posts").child(CurrentClubID);
        PostRef2=FirebaseDatabase.getInstance().getReference().child("Club Posts");
        ClubRef=FirebaseDatabase.getInstance().getReference().child("Club Details");


        CreateEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDate();
            }
        });

            GetClubInfo();
            PostCountingMethod();


    }

    private void PostCountingMethod() {
        PostRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
              {
                  String key=dataSnapshot1.getKey();
                  SecondCountingMethod(key);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SecondCountingMethod(String key) {
        PostRef2.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists())
               {
                   count=dataSnapshot.getChildrenCount();
               }
               else {
                   count=0;
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetClubInfo() {
        ClubRef.child(CurrentClubID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    CurrentClubName=dataSnapshot.child("name").getValue().toString();
                }

                if (dataSnapshot.hasChild("profile_picture"))
                {
                    CurrentClubProfilePicture=dataSnapshot.child("profile_picture").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SaveDate() {



        final String name=Event_Name.getText().toString();
        final String description=Event_Description.getText().toString();
        if (name.isEmpty())
        {
            Toast.makeText(this, "Events Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        if (description.isEmpty())
        {
            Toast.makeText(this, "Events Description is mandatory", Toast.LENGTH_SHORT).show();
        }
        else {


                    Calendar calForDate=Calendar.getInstance();
                    SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
                    String CurrentDate=currentDateFormat.format(calForDate.getTime());


                    Calendar calForTime=Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
                    String CurrentTime=currentTimeFormat.format(calForTime.getTime());

                    String postRefKey=PostRef.push().getKey();

                    HashMap<String ,Object> hashMap=new HashMap<>();
                    PostRef.updateChildren(hashMap);
                    PostKeyRef=PostRef.child(postRefKey);


                    HashMap<String,Object> hashMap1=new HashMap<>();
                    hashMap1.put("event_name",name);
                    hashMap1.put("post",description);
                    hashMap1.put("time",CurrentTime);
                    hashMap1.put("poster_name",CurrentClubName);
                    hashMap1.put("date",CurrentDate);
                    hashMap1.put("poster_profile_picture",CurrentClubProfilePicture);
                    hashMap1.put("type","event");
                    hashMap1.put("counter",count);
                    hashMap1.put("parent_uid",CurrentClubID);
                    hashMap1.put("child_uid",postRefKey);

                    PostKeyRef.updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(EventsMangeActivity.this, CurrentClubName+" event created successfully", Toast.LENGTH_SHORT).show();
                                Event_Name.setText("");
                                Event_Description.setText("");

                            }
                        }
                    });

                }


        }

}
