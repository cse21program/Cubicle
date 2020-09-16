package com.example.adminofcubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class StudentProfileActivity extends AppCompatActivity {
    private TextView StudentName1,StudentName2,StudentBatch,StudentDepartment,StudentUniversity,StudentPhone,StudentEmail,StudentID;
    private Toolbar student_toolbar;
    private CircleImageView Student_Image;
    private DatabaseReference ClubRef,MembershipRef;
    private String visit_student_id,CurrentClubID;
    private FirebaseAuth mAuth;
    private Button MessageBtn,MemberBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        ClubRef= FirebaseDatabase.getInstance().getReference().child("Student Details");
        MembershipRef= FirebaseDatabase.getInstance().getReference().child("Club Members");
        student_toolbar=findViewById(R.id.student_profile_toolbar);
        setSupportActionBar(student_toolbar);
        getSupportActionBar().setTitle("Student Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        CurrentClubID=mAuth.getCurrentUser().getUid();
        MessageBtn=findViewById(R.id.messageBtn);
        MemberBtn=findViewById(R.id.MemeberBtn);

        visit_student_id=getIntent().getExtras().get("visit_student_id").toString();
        student_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        StudentName1=findViewById(R.id.student_name);
        StudentName2=findViewById(R.id.student_name_2);
        StudentBatch=findViewById(R.id.student_batch);
        StudentDepartment=findViewById(R.id.student_department);
        StudentUniversity=findViewById(R.id.student_university);
        StudentPhone=findViewById(R.id.student_number);
        StudentEmail=findViewById(R.id.student_email);
        Student_Image=findViewById(R.id.student_image);
        StudentID=findViewById(R.id.StudentID);


        ClubRef.child(visit_student_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String batch=dataSnapshot.child("batch").getValue().toString();
                String department=dataSnapshot.child("department").getValue().toString();
                String university=dataSnapshot.child("university").getValue().toString();
                String phone=dataSnapshot.child("phone").getValue().toString();
                String email=dataSnapshot.child("email").getValue().toString();
                String id=dataSnapshot.child("id").getValue().toString();

                StudentName1.setText(name);
                StudentName2.setText(name);
                StudentBatch.setText(batch);
                StudentDepartment.setText(department);
                StudentUniversity.setText(university);
                StudentPhone.setText(phone);
                StudentEmail.setText(email);
                StudentID.setText(id);
                if (dataSnapshot.hasChild("image")){
                    final String image=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(Student_Image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).into(Student_Image);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            getMemberPosition();


    }

    private void getMemberPosition() {
        MembershipRef.child(visit_student_id).child(CurrentClubID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild("Club Members"))
              {
                  String positionMember=dataSnapshot.child("Club Members").getValue().toString();
                  if (positionMember.equals("Saved"))
                  {
                      MemberBtn.setVisibility(View.VISIBLE);
                      MessageBtn.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                              intent.putExtra("visit_student_id",visit_student_id);
                              startActivity(intent);
                          }
                      });
                      MessageBtn.setVisibility(View.VISIBLE);

                  }

              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
