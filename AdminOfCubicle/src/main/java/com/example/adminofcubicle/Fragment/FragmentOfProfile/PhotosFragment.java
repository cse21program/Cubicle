package com.example.adminofcubicle.Fragment.FragmentOfProfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.ImageViewerActivity;
import com.example.adminofcubicle.LikeAndCommentActivity;
import com.example.adminofcubicle.Model.PostItem;
import com.example.adminofcubicle.R;
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

public class PhotosFragment extends Fragment {
    private RecyclerView PhotoRecyclerview;
    private DatabaseReference ClubPostRef;
    private String CurrentClubID;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_photos, container, false);
        PhotoRecyclerview=view.findViewById(R.id.photosRecyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        PhotoRecyclerview.setLayoutManager(layoutManager);

        mAuth=FirebaseAuth.getInstance();
        CurrentClubID=mAuth.getCurrentUser().getUid();
        ClubPostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts").child(CurrentClubID);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostItem> options=new FirebaseRecyclerOptions.Builder<PostItem>()
                .setQuery(ClubPostRef,PostItem.class)
                .build();


        FirebaseRecyclerAdapter<PostItem,PhotoViewHolder> adapter=new FirebaseRecyclerAdapter<PostItem, PhotoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PhotoViewHolder holder, int position, @NonNull final PostItem model) {
                final String string=getRef(position).getKey();

                ClubPostRef.child(string).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild("type"))
                        {
                            final String type=dataSnapshot.child("type").getValue().toString();
                            final String poster_profile_picture=dataSnapshot.child("poster_profile_picture").getValue().toString();

                            if (type.equals("image"))
                            {
                                final String parent_uid=model.getParent_uid();
                                final String child_uid=model.getChild_uid();
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


                                final String name=dataSnapshot.child("poster_name").getValue().toString();
                                final String post=dataSnapshot.child("post").getValue().toString();
                                final String date=dataSnapshot.child("date").getValue().toString();
                                final String time=dataSnapshot.child("time").getValue().toString();
                                holder.PostLike.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",date+"\t"+time);
                                        intent.putExtra("type",type);;
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
                                        intent.putExtra("poster_name",name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",date+"\t"+time);
                                        intent.putExtra("type",type);
                                        startActivity(intent);
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

                                holder.Post.setVisibility(View.GONE);
                                holder.Poster_Name.setText(name);
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


                            else {

                                holder.Post.setVisibility(View.GONE);
                                holder.Poster_Name.setVisibility(View.GONE);
                                holder.PostLike.setVisibility(View.GONE);
                                holder.PostComments.setVisibility(View.GONE);
                                holder.TimeAndDate.setVisibility(View.GONE);
                                holder.Poster_Profile_Picture.setVisibility(View.GONE);
                                holder.cardView.setVisibility(View.GONE);
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
            public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
                PhotoViewHolder viewHolder=new PhotoViewHolder(view);
                return viewHolder;
            }
        };
        PhotoRecyclerview.setAdapter(adapter);
        adapter.startListening();
    }
    public static class PhotoViewHolder extends RecyclerView.ViewHolder{
        private TextView Post,Poster_Name,TimeAndDate,MoreActivity,LikeNumber,CommentNumber;
        private CircleImageView Poster_Profile_Picture;
        private ImageView Post_Image,PostLike,PostComments;
        private CardView cardView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            Post=itemView.findViewById(R.id.post);
            Poster_Name=itemView.findViewById(R.id.poster_name);
            TimeAndDate=itemView.findViewById(R.id.post_timeAndDate);
            MoreActivity=itemView.findViewById(R.id.moreActivity);
            PostLike=itemView.findViewById(R.id.post_likes);
            PostComments=itemView.findViewById(R.id.post_comments);
            Poster_Profile_Picture=itemView.findViewById(R.id.poster_profile_picture);
            Post_Image=itemView.findViewById(R.id.post_image);
            cardView=itemView.findViewById(R.id.cardview);
            LikeNumber=itemView.findViewById(R.id.likes_numbers);
            CommentNumber=itemView.findViewById(R.id.comment_number);
        }
    }
}
