package com.example.adminofcubicle.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminofcubicle.ImageViewerActivity;
import com.example.adminofcubicle.LikeAndCommentActivity;
import com.example.adminofcubicle.Model.PostItem;
import com.example.adminofcubicle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private Context mContext;
    private List<PostItem> itemList;
    private int likeCounter=0,commentCounter=0;

    public PostAdapter(Context mContext, List<PostItem> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
        PostViewHolder viewHolder=new PostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        final PostItem item=itemList.get(position);
        final String parent_uid=item.getParent_uid();
        final String child_uid=item.getChild_uid();
        final String type=item.getType();
        final String poster_name=item.getPoster_name();
        final String TimeAndDate=item.getTime()+"\t"+item.getDate();
        final String poster_profile_picture=item.getPoster_profile_picture();
        final String event_name=item.getEvent_name();
        final String post=item.getPost();
        if (type.equals("text"))
        {
            DatabaseReference PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
            PostRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        likeCounter = (int) dataSnapshot.getChildrenCount();
                        holder.likes_number.setText("Likes "+Integer.toString(likeCounter));

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
                    if (dataSnapshot.exists())
                    {
                        commentCounter= (int) dataSnapshot.getChildrenCount();
                        holder.Comments_number.setText("Comments "+Integer.toString(commentCounter));
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
                                holder.Post_likes.setImageResource(R.drawable.liked);
                            }
                            else {
                                holder.Post_likes.setImageResource(R.drawable.like);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            holder.Post_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, LikeAndCommentActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("poster_name",poster_name);
                    intent.putExtra("post",post);
                    intent.putExtra("type",type);
                    intent.putExtra("timeAndDate",TimeAndDate);
                    intent.putExtra("poster_profile_picture",poster_profile_picture);
                    mContext.startActivity(intent);
                }
            });
            holder.Post_Comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, LikeAndCommentActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("poster_name",poster_name);
                    intent.putExtra("post",post);
                    intent.putExtra("type",type);
                    intent.putExtra("timeAndDate",TimeAndDate);
                    intent.putExtra("poster_profile_picture",poster_profile_picture);
                    mContext.startActivity(intent);
                }
            });

            holder.Poster_Name.setText(poster_name);
            holder.TimeAndDate.setText(TimeAndDate);
            holder.Post.setText(post);

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
        if (type.equals("image"))
        {
            DatabaseReference PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
            PostRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        likeCounter = (int) dataSnapshot.getChildrenCount();
                        holder.likes_number.setText("Likes "+Integer.toString(likeCounter));

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
                    if (dataSnapshot.exists())
                    {
                        commentCounter= (int) dataSnapshot.getChildrenCount();
                        holder.Comments_number.setText("Comments "+Integer.toString(commentCounter));
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
                                holder.Post_likes.setImageResource(R.drawable.liked);
                            }
                            else {
                                holder.Post_likes.setImageResource(R.drawable.like);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            holder.Poster_Name.setText(poster_name);
            holder.TimeAndDate.setText(TimeAndDate);
            holder.Post.setVisibility(View.GONE);
            holder.Post_Image.setVisibility(View.VISIBLE);
            holder.Post_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, ImageViewerActivity.class);
                    intent.putExtra("post",post);
                    mContext.startActivity(intent);
                }
            });

            holder.Post_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, LikeAndCommentActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("poster_name",poster_name);
                    intent.putExtra("post",post);
                    intent.putExtra("type",type);
                    intent.putExtra("timeAndDate",TimeAndDate);
                    intent.putExtra("poster_profile_picture",poster_profile_picture);
                    mContext.startActivity(intent);
                }
            });
            holder.Post_Comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, LikeAndCommentActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("poster_name",poster_name);
                    intent.putExtra("post",post);
                    intent.putExtra("type",type);
                    intent.putExtra("timeAndDate",TimeAndDate);
                    intent.putExtra("poster_profile_picture",poster_profile_picture);
                    mContext.startActivity(intent);
                }
            });

            Picasso.get().load(post).networkPolicy(NetworkPolicy.OFFLINE).into(holder.Post_Image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(post).into(holder.Post_Image);
                }
            });

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
        if (type.equals("event"))

        {

            DatabaseReference PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts");
            PostRef.child(parent_uid).child(child_uid).child("Likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        likeCounter = (int) dataSnapshot.getChildrenCount();
                        holder.likes_number.setText("Likes "+Integer.toString(likeCounter));

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
                    if (dataSnapshot.exists())
                    {
                        commentCounter= (int) dataSnapshot.getChildrenCount();
                        holder.Comments_number.setText("Comments "+Integer.toString(commentCounter));
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
                                holder.Post_likes.setImageResource(R.drawable.liked);
                            }
                            else {
                                holder.Post_likes.setImageResource(R.drawable.like);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            holder.Poster_Name.setText(poster_name);
            holder.TimeAndDate.setText(TimeAndDate);
            holder.Event_Name.setVisibility(View.VISIBLE);
            holder.Event_Name.setText(event_name);
            holder.Post.setText(item.getPost());
            holder.Post_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, LikeAndCommentActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("poster_name",poster_name);
                    intent.putExtra("post",post);
                    intent.putExtra("type",type);
                    intent.putExtra("event_name",event_name);
                    intent.putExtra("timeAndDate",TimeAndDate);
                    intent.putExtra("poster_profile_picture",poster_profile_picture);
                    mContext.startActivity(intent);
                }
            });
            holder.Post_Comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, LikeAndCommentActivity.class);
                    intent.putExtra("parent_uid",parent_uid);
                    intent.putExtra("child_uid",child_uid);
                    intent.putExtra("poster_name",poster_name);
                    intent.putExtra("post",post);
                    intent.putExtra("type",type);
                    intent.putExtra("event_name",event_name);
                    intent.putExtra("timeAndDate",TimeAndDate);
                    intent.putExtra("poster_profile_picture",poster_profile_picture);
                    mContext.startActivity(intent);
                }
            });

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

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView Poster_Name,Post,TimeAndDate,likes_number,Comments_number,Event_Name;
        private CircleImageView Poster_Profile_Picture;
        private ImageView Post_Image,Post_likes,Post_Comments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            Poster_Name=itemView.findViewById(R.id.poster_name);
            Post=itemView.findViewById(R.id.post);
            TimeAndDate=itemView.findViewById(R.id.post_timeAndDate);
            Post_likes=itemView.findViewById(R.id.post_likes);
            likes_number=itemView.findViewById(R.id.likes_numbers);
            Post_Comments=itemView.findViewById(R.id.post_comments);
            Comments_number=itemView.findViewById(R.id.comment_number);
            Poster_Profile_Picture=itemView.findViewById(R.id.poster_profile_picture);
            Post_Image=itemView.findViewById(R.id.post_image);
            Event_Name=itemView.findViewById(R.id.event_name2);
        }
    }


}

