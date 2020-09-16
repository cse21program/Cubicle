package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private EditText UserName,UserBatch,UserDepartment,UserUniversity,UserPhoneNumber,UserEmail,StudentID;
    private Button ProfileUpdateButton;
    private FirebaseAuth  mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    private CircleImageView Profile_Image;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference("Student Profile Image");

        Toolbar toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InitializeFields();
        RetrieveUserInfo();
        ProfileUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();

            }
        });

        Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "For Profile Picture Update..Please go Profile and update your profile picture", Toast.LENGTH_SHORT).show();
            }
        });




    }





    private void UpdateSetting() {
        String name=UserName.getText().toString();
        String batch=UserBatch.getText().toString();
        String department=UserDepartment.getText().toString();
        String university=UserUniversity.getText().toString();
        String phone=UserPhoneNumber.getText().toString();
        String id=StudentID.getText().toString();
        if (name.isEmpty() && batch.isEmpty() && department.isEmpty() && university.isEmpty() && phone.isEmpty()  && id.isEmpty()){
            Toast.makeText(this, "Require All fields..", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("name",name);
            hashMap.put("batch",batch);
            hashMap.put("department",department);
            hashMap.put("university",university);
            hashMap.put("phone",phone);
            hashMap.put("id",id);
            hashMap.put("uid",currentUserID);
            RootRef.child("Student Details").child(currentUserID).updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendUserToProfileActivity();
                                Toast.makeText(SettingActivity.this, "Profile is update successfully", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                String messaage=task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error:"+messaage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    private void RetrieveUserInfo() {
        RootRef.child("Student Details").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                            final String image=dataSnapshot.child("image").getValue().toString();
                           ;
                            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(Profile_Image, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).into(Profile_Image);
                                }
                            });

                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String name=dataSnapshot.child("name").getValue().toString();
                            UserName.setText(name);
                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("batch")))
                        {
                            String batch=dataSnapshot.child("batch").getValue().toString();
                            UserBatch.setText(batch);
                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("department")))
                        {
                            String department=dataSnapshot.child("department").getValue().toString();
                            UserDepartment.setText(department);
                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("university")))
                        {
                            String university=dataSnapshot.child("university").getValue().toString();
                            UserUniversity.setText(university);
                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("email")))
                        {
                            String email=dataSnapshot.child("email").getValue().toString();
                            UserEmail.setText(email);
                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("phone")))
                        {
                            String phone=dataSnapshot.child("phone").getValue().toString();

                            UserPhoneNumber.setText(phone);
                        }
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("id")))
                        {
                            String id=dataSnapshot.child("id").getValue().toString();

                            StudentID.setText(id);
                        }
                        else {
                            Toast.makeText(SettingActivity.this, "Please update & set you profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void SendUserToProfileActivity() {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
    }

    private void InitializeFields() {
        UserName=findViewById(R.id.profile_name);
        UserBatch=findViewById(R.id.batch_name);
        UserDepartment=findViewById(R.id.department_name);
        UserUniversity=findViewById(R.id.university_name);
        UserPhoneNumber=findViewById(R.id.phone_number);
        UserEmail=findViewById(R.id.email_ID);
        ProfileUpdateButton=findViewById(R.id.profile_update_button);
        Profile_Image=findViewById(R.id.profile_image);
        StudentID=findViewById(R.id.Student_Id);
    }
}
