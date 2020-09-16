package com.example.adminofcubicle.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.Fragment.FragmentOfProfile.AboutFragment;
import com.example.adminofcubicle.Fragment.FragmentOfProfile.EventsFragment;
import com.example.adminofcubicle.Fragment.FragmentOfProfile.PhotosFragment;
import com.example.adminofcubicle.Fragment.FragmentOfProfile.PostsFragment;
import com.example.adminofcubicle.ProfileActivity;
import com.example.adminofcubicle.R;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private Button AboutButton,PostsButton,PhotosButton,EventsButton;
    private TextView profile_name;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    private ImageView Cover_image;
    private CircleImageView Profile_image;
    private static final int IMAGE_PICKER=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        AboutButton=view.findViewById(R.id.aboutId);
        PostsButton=view.findViewById(R.id.postid);
        PhotosButton=view.findViewById(R.id.photos);
        EventsButton=view.findViewById(R.id.EventsId);
        profile_name=view.findViewById(R.id.profile_name);
        Cover_image=view.findViewById(R.id.Cover_imageView);
        Profile_image=view.findViewById(R.id.Profile_imageView);

        storageReference= FirebaseStorage.getInstance().getReference("Club Profile Image");





        RootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AboutFragment());

            }
        });
        PhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            loadFragment(new PhotosFragment());
            }
        });
        PostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PostsFragment());

            }
        });
        EventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            loadFragment(new EventsFragment());
            }
        });
        RetrieveInfo();


        Cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();


            }
        });
        return view;


    }
    private void OpenImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_PICKER);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_PICKER && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
          imageUri=data.getData();
          if (uploadTask!=null && uploadTask.isInProgress()){
              Toast.makeText(getContext(), "Upload in  progress", Toast.LENGTH_SHORT).show();
          }
          else {
              UploadCoverImage();
          }
        }
    }

    private void UploadCoverImage() {
        final ProgressDialog uploadBar=new ProgressDialog(getContext());
        uploadBar.setMessage("Uploading....");
        uploadBar.setCanceledOnTouchOutside(false);
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

                        HashMap<String, Object> hashMap=new HashMap<>();
                        hashMap.put("cover_picture",mUri);
                        RootRef.child("Club Details").child(currentUserID).updateChildren(hashMap);



                        uploadBar.dismiss();
                        Toast.makeText(getContext(), "Cover Picture uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void RetrieveInfo() {
        RootRef.child("Club Details").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("name").exists())){

                    String name=dataSnapshot.child("name").getValue().toString();
                    profile_name.setText(name);
                }

                if (dataSnapshot.hasChild("profile_picture"))
                {
                    final String profile_picture =dataSnapshot.child("profile_picture").getValue().toString();
                    Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile_picture).into(Profile_image);
                        }
                    });
                }
                if (dataSnapshot.hasChild("cover_picture"))
                {
                    final String profile_picture =dataSnapshot.child("cover_picture").getValue().toString();
                    Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Cover_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile_picture).into(Cover_image);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentId,fragment);
        transaction.commit();
    }
}
