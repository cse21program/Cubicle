package com.example.adminofcubicle.Fragment.FragmentOfProfile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.EventListActivity;
import com.example.adminofcubicle.ImageViewerActivity;
import com.example.adminofcubicle.LikeAndCommentActivity;
import com.example.adminofcubicle.Model.PostItem;
import com.example.adminofcubicle.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class PostsFragment extends Fragment {
    private static final int IMAGE_PICKER = 1;
    private EditText Post_ET;
    private Button Post_Btn;
    private String Current_Club_Name,CurrentClubID,CurrentDate,CurrentTime,CurrentClubProfilePicture,CurrentClubMembers;
    private FirebaseAuth mAuth;
    private DatabaseReference ClubRef,PostsRef,PostsKeyRef,ClubMemberRef;
    private RecyclerView PostRecyclerview;
    private ImageView ImagePostBtn;
    private Uri ImageUri;
    private StorageTask uploadTask;
    private StorageReference  storageReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_posts, container, false);
       Post_ET=view.findViewById(R.id.Post_ET);
       Post_Btn=view.findViewById(R.id.postBtn);
       ImagePostBtn=view.findViewById(R.id.imagePostBtn);
       PostRecyclerview=view.findViewById(R.id.postsRecyclerview);
       LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
       layoutManager.setReverseLayout(true);
       PostRecyclerview.setLayoutManager(layoutManager);

       storageReference= FirebaseStorage.getInstance().getReference("Club Image Post");



        ImagePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });

       mAuth=FirebaseAuth.getInstance();
       CurrentClubID=mAuth.getCurrentUser().getUid();
       ClubRef=FirebaseDatabase.getInstance().getReference().child("Club Details");
       PostsRef=FirebaseDatabase.getInstance().getReference().child("Club Posts").child(CurrentClubID);
       ClubMemberRef=FirebaseDatabase.getInstance().getReference().child("Club Members");


       Post_Btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             ProfilePost();
           }
       });

       GetClubInfo();
       GetMembersInfo();


        return view;
    }

    private void GetMembersInfo() {
        ClubMemberRef.child(CurrentClubID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
             {
                 CurrentClubMembers=dataSnapshot1.getKey();
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void OpenImage() {
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
            ImageUri=data.getData();
            if (uploadTask!=null && uploadTask.isInProgress())
            {
                Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_SHORT).show();
            }
            else {
                UploadImage();
            }

        }
    }

    private void UploadImage() {

        final ProgressDialog uploadBar=new ProgressDialog(getContext());
        uploadBar.setMessage("Uploading....");
        uploadBar.show();

        if (ImageUri!=null)
        {
          final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(ImageUri)) ;
          uploadTask=fileReference.putFile(ImageUri);
          uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
              @Override
              public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot>task) throws Exception {
                  if (!task.isSuccessful())
                  {
                      throw task.getException();
                  }
                  return fileReference.getDownloadUrl();
              }
          }).addOnCompleteListener(new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {

                  Calendar calForDate=Calendar.getInstance();
                  SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
                  CurrentDate=currentDateFormat.format(calForDate.getTime());


                  Calendar calForTime=Calendar.getInstance();
                  SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
                  CurrentTime=currentTimeFormat.format(calForTime.getTime());
                  Uri downloadUri=task.getResult();
                  String mUri=downloadUri.toString();

                  String postRefKey=PostsRef.push().getKey();

                  HashMap<String ,Object> hashMap=new HashMap<>();
                  PostsRef.updateChildren(hashMap);
                  PostsKeyRef=PostsRef.child(postRefKey);

                  HashMap<String ,Object> postMap=new HashMap<>();
                  postMap.put("poster_name",Current_Club_Name);
                  postMap.put("post",mUri);
                  postMap.put("time",CurrentTime);
                  postMap.put("date",CurrentDate);
                  postMap.put("poster_profile_picture",CurrentClubProfilePicture);
                  postMap.put("type","image");
                  postMap.put("parent_uid",CurrentClubID);
                  postMap.put("child_uid",postRefKey);

                  PostsKeyRef.updateChildren(postMap);
                  uploadBar.dismiss();
                  Toast.makeText(getContext(), Current_Club_Name+" post update is successfully", Toast.LENGTH_SHORT).show();

              }
          }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                uploadBar.dismiss();
            }
        });
        }
        else {
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            uploadBar.dismiss();
        }
    }

    private void GetClubInfo() {
        ClubRef.child(CurrentClubID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Current_Club_Name=dataSnapshot.child("name").getValue().toString();
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

    private void ProfilePost()
    {
        final String post=Post_ET.getText().toString();
        final String postRefKey=PostsRef.push().getKey();
        if (post.isEmpty()){
            Toast.makeText(getContext(), "Please write something...", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
            CurrentDate=currentDateFormat.format(calForDate.getTime());


            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            CurrentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String ,Object> hashMap=new HashMap<>();
            PostsRef.updateChildren(hashMap);
            PostsKeyRef=PostsRef.child(postRefKey);

            final HashMap<String ,Object> postMap=new HashMap<>();
            postMap.put("poster_name",Current_Club_Name);
            postMap.put("post",post);
            postMap.put("time",CurrentTime);
            postMap.put("date",CurrentDate);
            postMap.put("poster_profile_picture",CurrentClubProfilePicture);
            postMap.put("type","text");
            postMap.put("parent_uid",CurrentClubID);
            postMap.put("child_uid",postRefKey);

            PostsKeyRef.updateChildren(postMap);
            Post_ET.setText("");
            Toast.makeText(getContext(), Current_Club_Name+" post update successfully", Toast.LENGTH_SHORT).show();


        }

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostItem> options=new FirebaseRecyclerOptions.Builder<PostItem>()
                .setQuery(PostsRef,PostItem.class)
                .build();
        FirebaseRecyclerAdapter<PostItem,PostsViewHolder> adapter=new FirebaseRecyclerAdapter<PostItem, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, final PostItem model) {
                final String string=getRef(position).getKey();
                final String parent_uid=model.getParent_uid();
                final String child_uid=model.getChild_uid();
                final String poster_name=model.getPoster_name();
                final String post=model.getPost();
                final String poster_profile_picture=model.getPoster_profile_picture();
                final String timeAndDate=model.getTime()+"\t"+model.getDate();
                PostsRef.child(string).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {


                            final String type=dataSnapshot.child("type").getValue().toString();
                            if (type.equals("text"))
                            {

                                DatabaseReference PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
                                PostRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int likeCounter=0;
                                        if (dataSnapshot.exists())
                                        {
                                            likeCounter = (int) dataSnapshot.getChildrenCount();
                                            holder.LikeNumber.setText("Likes "+Integer.toString(likeCounter));

                                        }
                                        else {
                                            likeCounter=0;

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostRef.child(parent_uid).child(child_uid).child("Comments").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int commentCounter=0;
                                        if (dataSnapshot.exists())
                                        {
                                            commentCounter= (int) dataSnapshot.getChildrenCount();
                                            holder.CommentNumber.setText("Comments "+Integer.toString(commentCounter));
                                        }
                                        else {
                                            commentCounter=0;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostRef.child(parent_uid).child(child_uid).child("Likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("position"))
                                                {
                                                    holder.PostLike.setImageResource(R.drawable.liked);
                                                }
                                                else {
                                                    holder.PostLike.setImageResource(R.drawable.like);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                holder.PostLike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("type",type);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",timeAndDate);
                                        startActivity(intent);
                                    }
                                });
                                holder.PostComments.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("type",type);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",timeAndDate);
                                        startActivity(intent);
                                    }
                                });


                                holder.Post.setText(post);
                                holder.PosterName.setText(poster_name);
                                holder.TimeAndDate.setText(timeAndDate);

                                if (dataSnapshot.hasChild("poster_profile_picture"))
                                {
                                    final String poster_profile_picture=dataSnapshot.child("poster_profile_picture").getValue().toString();
                                    Picasso.get().load(poster_profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Poster_Profile_Picture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(poster_profile_picture).into(holder.Poster_Profile_Picture);
                                        }
                                    });
                                }
                            }
                            if (type.equals("image"))
                            {

                                DatabaseReference PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
                                PostRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int likeCounter=0;
                                        if (dataSnapshot.exists())
                                        {
                                            likeCounter = (int) dataSnapshot.getChildrenCount();
                                            holder.LikeNumber.setText("Likes "+Integer.toString(likeCounter));

                                        }
                                        else {
                                            likeCounter=0;

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostRef.child(parent_uid).child(child_uid).child("Comments").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int commentCounter=0;
                                        if (dataSnapshot.exists())
                                        {
                                            commentCounter= (int) dataSnapshot.getChildrenCount();
                                            holder.CommentNumber.setText("Comments "+Integer.toString(commentCounter));
                                        }
                                        else {
                                            commentCounter=0;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostRef.child(parent_uid).child(child_uid).child("Likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("position"))
                                                {
                                                    holder.PostLike.setImageResource(R.drawable.liked);
                                                }
                                                else {
                                                    holder.PostLike.setImageResource(R.drawable.like);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                holder.Post_Image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), ImageViewerActivity.class);
                                        intent.putExtra("post",post);
                                        startActivity(intent);
                                    }
                                });
                                holder.PostLike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("type",type);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",timeAndDate);
                                        startActivity(intent);
                                    }
                                });
                                holder.PostComments.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",timeAndDate);
                                        intent.putExtra("type",type);
                                        startActivity(intent);
                                        startActivity(intent);
                                    }
                                });
                                String poster_name=dataSnapshot.child("poster_name").getValue().toString();
                                final String post=dataSnapshot.child("post").getValue().toString();
                                final String date=dataSnapshot.child("date").getValue().toString();
                                String time=dataSnapshot.child("time").getValue().toString();

                                holder.Post.setVisibility(View.GONE);
                                holder.PosterName.setText(poster_name);
                                holder.TimeAndDate.setText(time+"\t"+date);
                                holder.Post_Image.setVisibility(View.VISIBLE);
                                Picasso.get().load(post).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Post_Image, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(post).into(holder.Post_Image);
                                    }
                                });


                                if (dataSnapshot.hasChild("poster_profile_picture"))
                                {
                                    final String profile_picture=dataSnapshot.child("poster_profile_picture").getValue().toString();
                                    Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Poster_Profile_Picture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(profile_picture).into(holder.Poster_Profile_Picture);
                                        }
                                    });
                                }

                            }
                            if (type.equals("event")){
                                final String parent_uid=dataSnapshot.child("parent_uid").getValue().toString();
                                final String child_uid=dataSnapshot.child("child_uid").getValue().toString();
                                final String event_name=dataSnapshot.child("event_name").getValue().toString();

                                DatabaseReference PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
                                PostRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int likeCounter=0;
                                        if (dataSnapshot.exists())
                                        {
                                            likeCounter = (int) dataSnapshot.getChildrenCount();
                                            holder.LikeNumber.setText("Likes "+Integer.toString(likeCounter));

                                        }
                                        else {
                                            likeCounter=0;

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostRef.child(parent_uid).child(child_uid).child("Comments").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int commentCounter=0;
                                        if (dataSnapshot.exists())
                                        {
                                            commentCounter= (int) dataSnapshot.getChildrenCount();
                                            holder.CommentNumber.setText("Comments "+Integer.toString(commentCounter));
                                        }
                                        else {
                                            commentCounter=0;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostRef.child(parent_uid).child(child_uid).child("Likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("position"))
                                                {
                                                    holder.PostLike.setImageResource(R.drawable.liked);
                                                }
                                                else {
                                                    holder.PostLike.setImageResource(R.drawable.like);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                holder.PostLike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("event_name",event_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",timeAndDate);
                                        intent.putExtra("type",type);
                                        startActivity(intent);
                                    }
                                });
                                holder.PostComments.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",timeAndDate);
                                        intent.putExtra("type",type);
                                        intent.putExtra("event_name",event_name);
                                        startActivity(intent);
                                    }
                                });
                                String time=dataSnapshot.child("time").getValue().toString();
                                String date=dataSnapshot.child("date").getValue().toString();
                                String post=dataSnapshot.child("post").getValue().toString();
                                String poster_name=dataSnapshot.child("poster_name").getValue().toString();
                                holder.Event_Name.setVisibility(View.VISIBLE);
                                holder.Event_Name.setText(event_name);
                                holder.WhoIsRegistered.setVisibility(View.VISIBLE);
                                holder.WhoIsRegistered.setText("See who is register "+event_name);
                                holder.WhoIsRegistered.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), EventListActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("event_name",event_name);
                                        startActivity(intent);
                                    }
                                });
                                holder.TimeAndDate.setText(time+"\t"+date);
                                holder.Post.setText(post);
                                holder.PosterName.setText(poster_name);




                                if (dataSnapshot.hasChild("poster_profile_picture"))
                                {
                                    final String profile_picture=dataSnapshot.child("poster_profile_picture").getValue().toString();
                                    Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Poster_Profile_Picture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(profile_picture).into(holder.Poster_Profile_Picture);
                                        }
                                    });
                                }
                            }
                        }





                        holder.MoreActivity.setVisibility(View.VISIBLE);
                        holder.MoreActivity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[]=new CharSequence[]
                                {
                                    "Delete",
                                    "Cancel"
                                };
                                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                      if (i==0) {
                                         DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();

                                         RootRef.child("Club Posts").child(CurrentClubID).child(string).removeValue()
                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                              if (task.isSuccessful()) {
                                                                  Toast.makeText(getContext(), "Post Deleted successfully..", Toast.LENGTH_SHORT).show();
                                                              }
                                                          }
                                                      });







                                      }
                                      if (i==1){
                                          Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                      }

                                    }
                                });
                                builder.setCancelable(true);
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
                PostsViewHolder viewHolder=new PostsViewHolder(view);
                return viewHolder;
            }
        };
        PostRecyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        private TextView Post,PosterName,TimeAndDate,MoreActivity,Event_Name,WhoIsRegistered,LikeNumber,CommentNumber;
        private CircleImageView Poster_Profile_Picture;
        private ImageView Post_Image,PostLike,PostComments;
        private CardView cardView;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            WhoIsRegistered=itemView.findViewById(R.id.whoRegisteredEvent);
            Post=itemView.findViewById(R.id.post);
            PosterName=itemView.findViewById(R.id.poster_name);
            TimeAndDate=itemView.findViewById(R.id.post_timeAndDate);
            MoreActivity=itemView.findViewById(R.id.moreActivity);
            PostLike=itemView.findViewById(R.id.post_likes);
            PostComments=itemView.findViewById(R.id.post_comments);
            Poster_Profile_Picture=itemView.findViewById(R.id.poster_profile_picture);
            Post_Image=itemView.findViewById(R.id.post_image);
            cardView=itemView.findViewById(R.id.cardview);
            Event_Name=itemView.findViewById(R.id.event_name2);
            LikeNumber=itemView.findViewById(R.id.likes_numbers);
            CommentNumber=itemView.findViewById(R.id.comment_number);

        }
    }
}
