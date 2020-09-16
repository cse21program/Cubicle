package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cubicle.Model.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventRegistrationActivity extends AppCompatActivity {
    private TextView Event_Details;
    private EditText StudentId,StudentPhone,StudentEmail;
    private Button RegisterBtn;
    private DatabaseReference StudentRef,PostRef,RegisterRef;
    private FirebaseAuth mAuth;
    private String CurrentUserID,CurrentUserName,CurrentUserProfilePicture,parent_uid,child_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        StudentRef= FirebaseDatabase.getInstance().getReference().child("Student Details");
        PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();

        Event_Details=findViewById(R.id.eventDetails);
        StudentId=findViewById(R.id.studentId);
        StudentPhone=findViewById(R.id.studentPhone);
        StudentEmail=findViewById(R.id.student_email);
        RegisterBtn=findViewById(R.id.RegisterBtn);


        String event_name=getIntent().getExtras().get("event_name").toString();
        parent_uid=getIntent().getExtras().get("parent_uid").toString();
        child_uid=getIntent().getExtras().get("child_uid").toString();

        Event_Details.setText(event_name+" event registration on going");

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterHere();
            }
        });


        GetUserInfo();







    }

    private void RegisterInfo() {
        RegisterRef= PostRef.child(parent_uid).child(child_uid).child("Register Members").child(CurrentUserID);
        RegisterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String reg_type=dataSnapshot.child("reg_type").getValue().toString();
                    if (reg_type.equals("Approved"))
                    {
                        String phone=dataSnapshot.child("phone").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        String studentId=dataSnapshot.child("studentId").getValue().toString();
                        RegisterBtn.setText(reg_type);
                        RegisterBtn.setBackgroundColor(getResources().getColor(R.color.colorDefault));
                        RegisterBtn.setEnabled(false);
                        StudentId.setEnabled(false);
                        StudentId.setText(studentId);
                        StudentEmail.setEnabled(false);
                        StudentEmail.setText(email);
                        StudentPhone.setText(phone);
                        StudentPhone.setEnabled(false);

                    }
                    else {

                        String phone=dataSnapshot.child("phone").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        String studentId=dataSnapshot.child("studentId").getValue().toString();
                        RegisterBtn.setText(reg_type);
                        StudentId.setText(studentId);
                        StudentEmail.setText(email);
                        StudentPhone.setText(phone);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void RegisterHere() {
        String studentId=StudentId.getText().toString();
        String phone=StudentPhone.getText().toString();
        String email=StudentEmail.getText().toString();
        String RegisterRefKey=PostRef.push().getKey();
        if (studentId.isEmpty())
        {
            Toast.makeText(this, "All fil Require..", Toast.LENGTH_SHORT).show();
        }
        if (phone.isEmpty())
        {
            Toast.makeText(this, "All fil Require..", Toast.LENGTH_SHORT).show();
        }
        if (email.isEmpty())
        {
            Toast.makeText(this, "All fil Require..", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
            String CurrentDate=currentDateFormat.format(calForDate.getTime());


            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("HH:mm a");
            String CurrentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String ,Object> hashMap=new HashMap<>();
            PostRef.updateChildren(hashMap);
            RegisterRef=PostRef.child(parent_uid).child(child_uid).child("Register Members").child(CurrentUserID);

            HashMap<String ,Object> postMap=new HashMap<>();
            postMap.put("studentId",studentId);
            postMap.put("phone",phone);
            postMap.put("email",email);
            postMap.put("date",CurrentDate);
            postMap.put("time",CurrentTime);
            postMap.put("profile_picture",CurrentUserProfilePicture);
            postMap.put("name",CurrentUserName);
            postMap.put("reg_type","Submitted");
            RegisterRef.updateChildren(postMap);

            Toast.makeText(this, "Registration is successfully", Toast.LENGTH_SHORT).show();


        }
    }

    private void GetUserInfo() {
        StudentRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RegisterInfo();

                if (dataSnapshot.exists())
                {
                    CurrentUserName=dataSnapshot.child("name").getValue().toString();

                }
                if (dataSnapshot.hasChild("image"))
                {
                    CurrentUserProfilePicture=dataSnapshot.child("image").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
