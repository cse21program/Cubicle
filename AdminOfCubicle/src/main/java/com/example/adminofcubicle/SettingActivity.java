package com.example.adminofcubicle;

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
import android.widget.ImageView;
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
    private EditText Club_Name,Club_Mission,Club_Email,Club_Website,Club_Phone,Club_Founded;
    private Button Update_information_btn;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CircleImageView Profile_Picture;
    private static final int IMAGE_PICKER=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        RootRef=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        storageReference= FirebaseStorage.getInstance().getReference("Club Profile Image");

        Toolbar toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InitilizeFields();
Update_information_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(SettingActivity.this, "Update your profile picture then update your profile information", Toast.LENGTH_SHORT).show();
    }
});


        Profile_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });

       RetrieveClubInformation();
}

    private void OpenImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_PICKER);
    }
    private String getFileExtension (Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_PICKER && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            if (uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            }
            else {
                UploadProfileImage();
            }
        }
    }

    private void UploadProfileImage() {
        final ProgressDialog uploadBar=new ProgressDialog(this);
        uploadBar.setMessage("Uploading....");
        uploadBar.show();

        if (imageUri!=null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("profile_picture",mUri);
                        RootRef.child("Club Details").child(currentUserID).updateChildren(hashMap);
                        uploadBar.dismiss();
                        Toast.makeText(SettingActivity.this, "Profile_Picture Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(SettingActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        uploadBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingActivity.this, "Exception: "+e, Toast.LENGTH_SHORT).show();
                    uploadBar.dismiss();
                }
            });

        }
    }

    private void RetrieveClubInformation()
    {
        RootRef.child("Club Details").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) ){
                    String name=dataSnapshot.child("name").getValue().toString();
                    Club_Name.setText(name);

                }
             if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("mission"))){
                 String mission=dataSnapshot.child("mission").getValue().toString();
                 Club_Mission.setText(mission);
             }
            if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("email"))){
                String email=dataSnapshot.child("email").getValue().toString();
                      Club_Email.setText(email);
             }
         if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("website"))){
                 String website=dataSnapshot.child("website").getValue().toString();
             Club_Website.setText(website);
            }
         if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("phone"))){
                 String phone=dataSnapshot.child("phone").getValue().toString();
                 Club_Phone.setText(phone);
           }
          if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("founded"))){
                  String founded=dataSnapshot.child("founded").getValue().toString();
                  Club_Founded.setText(founded);
             }
          if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("profile_picture"))){

              Update_information_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      UpdateSettings();
                  }
              });

                    final String cover_picture=dataSnapshot.child("profile_picture").getValue().toString();
                    Picasso.get().load(cover_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Profile_Picture, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(cover_picture).into(Profile_Picture);
                        }
                    });

                }





 }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitilizeFields()
    {
        Club_Name=findViewById(R.id.club_name);
        Club_Mission=findViewById(R.id.club_mission);
        Club_Email=findViewById(R.id.club_email);
        Club_Website=findViewById(R.id.club_websit);
        Club_Phone=findViewById(R.id.club_phone);
        Club_Founded=findViewById(R.id.club_founded);

        Profile_Picture=findViewById(R.id.profile_image);
        Update_information_btn=findViewById(R.id.information_update_btn);
    }
    private void UpdateSettings()
    {
       String name=Club_Name.getText().toString();
        String mission=Club_Mission.getText().toString();
        String phone=Club_Phone.getText().toString();
        String email=Club_Email.getText().toString();
        String website=Club_Website.getText().toString();
        String founded=Club_Founded.getText().toString();

        if (name.isEmpty()){
            Toast.makeText(this, "Club Name is Mandatory..Please Write the club name ..", Toast.LENGTH_SHORT).show();
        }
        else if (mission.isEmpty()){
            Toast.makeText(this, "Mission is Mandatory..Please Write the mission..", Toast.LENGTH_SHORT).show();

        }
        else {
            HashMap<String,Object> profileDetails=new HashMap<>();
            profileDetails.put("name",name);
            profileDetails.put("mission",mission);
            profileDetails.put("phone",phone);
            profileDetails.put("email",email);
            profileDetails.put("website",website);
            profileDetails.put("founded",founded);
            profileDetails.put("uid",currentUserID);

            RootRef.child("Club Details").child(currentUserID).updateChildren(profileDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SettingActivity.this, "Profile information update successfully", Toast.LENGTH_SHORT).show();
                                SendUserProfileActivity();
                            }
                            else {
                                String message=task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Exception: "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserProfileActivity() {
        Intent intent=new Intent(SettingActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

}
