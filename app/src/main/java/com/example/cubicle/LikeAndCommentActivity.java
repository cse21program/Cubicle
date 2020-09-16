package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cubicle.Model.CommentItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAndCommentActivity extends AppCompatActivity {
    private EditText Comment_ET;
    private ImageView Comment_Push;
    private TextView LikeNumber,CommentNumber;
    private DatabaseReference PostsRef,PostsKeyRef,StudentsRef;
    private FirebaseAuth mAuth;
    private ImageView PostLike,PostComment;
    private RecyclerView LikeAndCommentRecyclerView;
    private String CurrentUserID,parent_uid,child_uid,CurrentUserName,CurrentUSerProfilePicture,Current_Like_Position="unlike";
    private String poster_name,post,poster_profile_picture,type,timeAndDate;
    private int countComment=0,likeCounter=0;
    private TextView Poster_Name,Post,TimeAndDate,likes_number,Comments_number,Event_Name,WhoRegister;
    private CircleImageView Poster_Profile_Picture;
    private ImageView Post_Image,Post_Comments,Post_likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_and_comment);

        Poster_Name=findViewById(R.id.poster_name);
        Post=findViewById(R.id.post);
        TimeAndDate=findViewById(R.id.post_timeAndDate);
        Post_likes=findViewById(R.id.post_likes);
        likes_number=findViewById(R.id.likes_number);
        Post_Comments=findViewById(R.id.post_comments);
        Comments_number=findViewById(R.id.comments_number);
        Poster_Profile_Picture=findViewById(R.id.poster_profile_picture);
        Post_Image=findViewById(R.id.post_image);
        Event_Name=findViewById(R.id.event_name2);
        WhoRegister=findViewById(R.id.whoRegisteredEvent);

        PostLike=findViewById(R.id.PostLike);
        LikeNumber=findViewById(R.id.LikeNumber);
        PostComment=findViewById(R.id.PostComments);
        CommentNumber=findViewById(R.id.CommentNumbers);
        LikeAndCommentRecyclerView=findViewById(R.id.LikeAndComments_recyclerview);
        LikeAndCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Comment_ET=findViewById(R.id.Comments_ET);
        Comment_Push=findViewById(R.id.comment_push);

        parent_uid=getIntent().getExtras().get("parent_uid").toString();
        child_uid=getIntent().getExtras().get("child_uid").toString();

        post=getIntent().getExtras().get("post").toString();
        poster_name=getIntent().getExtras().get("poster_name").toString();
        poster_profile_picture=getIntent().getExtras().get("poster_profile_picture").toString();
        type=getIntent().getExtras().get("type").toString();
        timeAndDate=getIntent().getExtras().get("timeAndDate").toString();



        PostsRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
        StudentsRef=FirebaseDatabase.getInstance().getReference().child("Student Details");

        mAuth= FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();






        Comment_Push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment_Here();
            }
        });
        PostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Current_Like_Position.equals("unlike"))
                {
                    LikePost();

                }
                if (Current_Like_Position.equals("like"))
                {
                    UnlikeMethod();
                }

            }
        });

        GetUserInfo();
        GetCommentInfo();
        GetLikeCounter();
        GetLikePosition();
        getPostInfo();



    }

    private void getPostInfo() {
        if (type.equals("text"))
        {
            Poster_Name.setText(poster_name);
            TimeAndDate.setText(timeAndDate);
            Post.setText(post);

            Picasso.get().load(poster_profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Poster_Profile_Picture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(poster_profile_picture).into(Poster_Profile_Picture);
                }
            });


        }
        if (type.equals("image"))
        {
            Post_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),ImageViewerActivity.class);
                    intent.putExtra("post",post);
                    startActivity(intent);
                }
            });


            Poster_Name.setText(poster_name);
            TimeAndDate.setText(timeAndDate);
            Post.setVisibility(View.GONE);
            Post_Image.setVisibility(View.VISIBLE);
            Picasso.get().load(poster_profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Poster_Profile_Picture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(poster_profile_picture).into(Poster_Profile_Picture);
                }
            });

            Picasso.get().load(post).networkPolicy(NetworkPolicy.OFFLINE).into(Post_Image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                Picasso.get().load(post).into(Post_Image);
                }
            });


        }
        if (type.equals("event"))
        {
            final String  event_name=getIntent().getExtras().get("event_name").toString();


            Picasso.get().load(poster_profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Poster_Profile_Picture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(poster_profile_picture).into(Poster_Profile_Picture);
                }
            });
            Poster_Name.setText(poster_name);
            Post.setText(post);
            Event_Name.setVisibility(View.VISIBLE);
          Event_Name.setText(event_name);
            WhoRegister.setVisibility(View.VISIBLE);
          WhoRegister.setText(event_name+" event registration is on going..");
            WhoRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),EventRegistrationActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("event_name",event_name);
                    startActivity(intent);
                }
            });
        }

    }

    private void GetLikePosition() {
        PostsRef.child(parent_uid).child(child_uid).child("Likes").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("position"))
                {
                    Current_Like_Position="like";
                    PostLike.setImageResource(R.drawable.liked);

                }
                else {
                    Current_Like_Position="unlike";
                    PostLike.setImageResource(R.drawable.like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UnlikeMethod() {
        PostsRef.child(parent_uid).child(child_uid).child("Likes").child(CurrentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    PostLike.setImageResource(R.drawable.like);
                    Current_Like_Position="unlike";
                    Toast.makeText(LikeAndCommentActivity.this, "Post Unlike", Toast.LENGTH_SHORT).show();

                }
                else {
                    Current_Like_Position="like";
                    PostLike.setImageResource(R.drawable.liked);
                }
            }
        });
    }

    private void LikePost() {
        HashMap<String,Object> likeMap=new HashMap<>();
        likeMap.put("position","Liked");

        PostsRef.child(parent_uid).child(child_uid).child("Likes").child(CurrentUserID).updateChildren(likeMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(LikeAndCommentActivity.this, "Post liked", Toast.LENGTH_SHORT).show();
                            Current_Like_Position="like";
                            PostLike.setImageResource(R.drawable.liked);
                        }
                        else {
                            Toast.makeText(LikeAndCommentActivity.this, "Error occurred: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void GetUserInfo() {
        StudentsRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists())
                {
                    CurrentUserName=dataSnapshot.child("name").getValue().toString();
                    CurrentUSerProfilePicture=dataSnapshot.child("image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void GetLikeCounter() {

        PostsRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    likeCounter= (int) dataSnapshot.getChildrenCount();
                    LikeNumber.setText("Likes "+Integer.toString(likeCounter));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetCommentInfo() {
        PostsRef.child(parent_uid).child(child_uid).child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    countComment= (int) dataSnapshot.getChildrenCount();
                    CommentNumber.setText("Comments "+Integer.toString(countComment));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Comment_Here() {
        String comments=Comment_ET.getText().toString();
        String postKey=PostsRef.push().getKey();
        if (comments.isEmpty()){
            Toast.makeText(this, "Write something..", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MM dd,yyyy");
            String CurrentDate=currentDateFormat.format(calForDate.getTime());


            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            String CurrentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String ,Object> hashMap=new HashMap<>();
            PostsRef.updateChildren(hashMap);
            PostsKeyRef=PostsRef.child(parent_uid).child(child_uid).child("Comments").child(postKey);

            HashMap<String,Object> hashMap2=new HashMap<>();
            hashMap2.put("time",CurrentTime);
            hashMap2.put("comment",comments);
            hashMap2.put("date",CurrentDate);
            hashMap2.put("name",CurrentUserName);
            hashMap2.put("profile_picture",CurrentUSerProfilePicture);


            PostsKeyRef.updateChildren(hashMap2);

            Toast.makeText(this, "Comment update successfully", Toast.LENGTH_SHORT).show();
            Comment_ET.setText("");

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<CommentItem> options=new FirebaseRecyclerOptions.Builder<CommentItem>()
                .setQuery(PostsRef.child(parent_uid).child(child_uid).child("Comments"),CommentItem.class)
                .build();

        FirebaseRecyclerAdapter<CommentItem,CommentsViewHolder> adapter=new FirebaseRecyclerAdapter<CommentItem, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder holder, int position, @NonNull final CommentItem model) {
                holder.Name.setText(model.getName());
                holder.comment.setText(model.getComment());
                holder.TimeAndDate.setText(model.getTime()+"\t"+model.getDate());
                Picasso.get().load(model.getProfile_picture()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profile_Picture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(model.getProfile_picture()).into(holder.profile_Picture);
                    }
                });

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
                CommentsViewHolder viewHolder=new CommentsViewHolder(view);
                return viewHolder;
            }
        };
        LikeAndCommentRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profile_Picture;
        private TextView Name,TimeAndDate,comment;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_Picture=itemView.findViewById(R.id.commentor_profile_picture);
            Name=itemView.findViewById(R.id.commentor_name);
            TimeAndDate=itemView.findViewById(R.id.comment_timeAndDate);
            comment=itemView.findViewById(R.id.commentline);

        }
    }
}
