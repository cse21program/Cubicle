package com.example.cubicle.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.cubicle.R;
import com.example.cubicle.SettingActivity;
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

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private TextView UserName,UserName2,UserBatch,UserDepartment,UserUniversity,UserPhone,UserEmail,StudentID;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private CircleImageView ProfileImage;
    private String currentUserID;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        UserName=view.findViewById(R.id.user_name);
        UserName2=view.findViewById(R.id.profile_name);
        UserBatch=view.findViewById(R.id.user_batch);
        UserDepartment=view.findViewById(R.id.user_department);
        UserUniversity=view.findViewById(R.id.user_university);
        UserPhone=view.findViewById(R.id.phone_number);
        UserEmail=view.findViewById(R.id.email_ID);
        StudentID=view.findViewById(R.id.Student_Id);
        RootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        ProfileImage=view.findViewById(R.id.profile_image);
        storageReference= FirebaseStorage.getInstance().getReference("Student Profile Image");
        currentUserID=mAuth.getCurrentUser().getUid();

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });
        RetrieveUserProfileInfo();
        return view;
    }

    private void OpenImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }



    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            if (uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_SHORT).show();
            }else {
                UploadImage();
            }
        }
    }
    private void UploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading....");
        pd.show();
        if (imageUri!=null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task <UploadTask.TaskSnapshot>task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri>task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("image",mUri);
                        RootRef.child("Student Details").child(currentUserID).updateChildren(hashMap);
                        Toast.makeText(getContext(), "Profile Picture Uploaded successfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }


    private void RetrieveUserProfileInfo() {
        RootRef.child("Student Details").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    final String image=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                        Picasso.get().load(image).into(ProfileImage);
                        }
                    });
                }
               if ((dataSnapshot.exists())){
                    String name=dataSnapshot.child("name").getValue().toString();
                    String id=dataSnapshot.child("id").getValue().toString();
                    UserName.setText(name);
                    UserName2.setText(name);
                    StudentID.setText(id);
                }
                 if ((dataSnapshot.exists())  && (dataSnapshot.hasChild("batch"))  && (dataSnapshot.hasChild("department"))  && (dataSnapshot.hasChild("university"))  && (dataSnapshot.hasChild("phone"))  && (dataSnapshot.hasChild("email")))
                {
                    String batch=dataSnapshot.child("batch").getValue().toString();
                    String department=dataSnapshot.child("department").getValue().toString();
                    String university=dataSnapshot.child("university").getValue().toString();
                    String phone=dataSnapshot.child("phone").getValue().toString();
                    String email=dataSnapshot.child("email").getValue().toString();
                    UserBatch.setText(batch);
                    UserDepartment.setText(department);
                    UserUniversity.setText(university);
                    UserPhone.setText(phone);
                    UserEmail.setText(email);

                }
                else {
                    Toast.makeText(getContext(), "Please set your profile setting", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });}
}
