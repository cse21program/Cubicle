package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cubicle.Model.PostItem;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAndChatActivity extends AppCompatActivity {
    private ImageView coverImage;
    private CircleImageView profile_image;
    private RecyclerView PostAndCommentRecyclerView;
    private TextView ClubName,PostTitle;
    private Button MessageBtn,MemberBtn;
    private String visit_club_id,getVisiter;
    private DatabaseReference ClubDetailsRef,ClubPosts;
    private Toolbar PostAndChatToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_and_chat);
        coverImage=findViewById(R.id.post_and_chat_coverImage);
        profile_image=findViewById(R.id.post_and_chat_profile_picture);
        PostAndCommentRecyclerView=findViewById(R.id.post_and_chat_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        PostAndCommentRecyclerView.setLayoutManager(layoutManager);
        ClubName=findViewById(R.id.postAndChat_clubName);
        PostTitle=findViewById(R.id.postTitle);
        MessageBtn=findViewById(R.id.messageBtn);
        MemberBtn=findViewById(R.id.memberBtn);
        PostAndChatToolBar=findViewById(R.id.post_chat_toolbar);

        setSupportActionBar(PostAndChatToolBar);
        getSupportActionBar().setTitle("Club Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PostAndChatToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        visit_club_id= getIntent().getExtras().get("visit_club_id").toString();
        ClubDetailsRef= FirebaseDatabase.getInstance().getReference().child("Club Details");
        ClubPosts= FirebaseDatabase.getInstance().getReference().child("Club Posts").child(visit_club_id);
        MessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                    intent.putExtra("visit",visit_club_id);
                    startActivity(intent);
            }
        });



        getClubInfo();
    }

    private void getClubInfo() {
        ClubDetailsRef.child(visit_club_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String name=dataSnapshot.child("name").getValue().toString();

               ClubName.setText(name);
               if ((dataSnapshot.hasChild("profile_picture")) && (dataSnapshot.hasChild("cover_picture")))
               {
                   final String cover=dataSnapshot.child("cover_picture").getValue().toString();
                   final String profile=dataSnapshot.child("profile_picture").getValue().toString();

                   Picasso.get().load(profile).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
                       @Override
                       public void onSuccess() {

                       }

                       @Override
                       public void onError(Exception e) {
                    Picasso.get().load(profile).into(profile_image);
                       }
                   });

                   Picasso.get().load(cover).networkPolicy(NetworkPolicy.OFFLINE).into(coverImage, new Callback() {
                       @Override
                       public void onSuccess() {

                       }

                       @Override
                       public void onError(Exception e) {
                           Picasso.get().load(cover).into(coverImage);
                       }
                   });

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<PostItem> options=new FirebaseRecyclerOptions.Builder<PostItem>()
                .setQuery(ClubPosts,PostItem.class)
                .build();
        FirebaseRecyclerAdapter<PostItem,PostAndChatViewHolder> adapter=new FirebaseRecyclerAdapter<PostItem, PostAndChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostAndChatViewHolder holder, int position, @NonNull PostItem model) {
                final String string=getRef(position).getKey();
                final String parent_uid=model.getParent_uid();
                final String child_uid=model.getChild_uid();
                final String poster_name=model.getPoster_name();
                final String post=model.getPost();
                final String poster_profile_picture=model.getPoster_profile_picture();
                final String timeAndDate=model.getTime()+"\t"+model.getDate();
                ClubPosts.child(string).addValueEventListener(new ValueEventListener() {
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
                                        Intent intent=new Intent(getApplicationContext(), LikeAndCommentActivity.class);
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
                                        Intent intent=new Intent(getApplicationContext(), LikeAndCommentActivity.class);
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
                                        Intent intent=new Intent(getApplicationContext(), ImageViewerActivity.class);
                                        intent.putExtra("post",post);
                                        startActivity(intent);
                                    }
                                });
                                holder.PostLike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getApplicationContext(), LikeAndCommentActivity.class);
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
                                        Intent intent=new Intent(getApplicationContext(), LikeAndCommentActivity.class);
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
                                        Intent intent=new Intent(getApplicationContext(), LikeAndCommentActivity.class);
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
                                        Intent intent=new Intent(getApplicationContext(), LikeAndCommentActivity.class);
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public PostAndChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_layout,parent,false);
                PostAndChatViewHolder viewHolder=new PostAndChatViewHolder(view);
                return viewHolder;
            }
        };
        PostAndCommentRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    class  PostAndChatViewHolder extends RecyclerView.ViewHolder{
        private TextView Post,PosterName,TimeAndDate,MoreActivity,Event_Name,WhoIsRegistered,LikeNumber,CommentNumber;
        private CircleImageView Poster_Profile_Picture;
        private ImageView Post_Image,PostLike,PostComments;
        private CardView cardView;

        public PostAndChatViewHolder(@NonNull View itemView) {
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
            Event_Name=itemView.findViewById(R.id.event_name2);
            LikeNumber=itemView.findViewById(R.id.likes_number);
            CommentNumber=itemView.findViewById(R.id.comments_number);
        }
    }
}
