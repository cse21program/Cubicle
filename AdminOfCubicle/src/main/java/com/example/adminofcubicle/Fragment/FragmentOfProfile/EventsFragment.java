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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adminofcubicle.EventListActivity;
import com.example.adminofcubicle.EventsMangeActivity;
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


public class EventsFragment extends Fragment {
    private LinearLayout create_event_layout;
    private RecyclerView event_recyclerview;
    private DatabaseReference PostRef,PostRef2;
    private FirebaseAuth mAuth;
    private String CurrentClubID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_events, container, false);
       create_event_layout=view.findViewById(R.id.createEventLayout);
       event_recyclerview=view.findViewById(R.id.eventRecyclerview);
       LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
       layoutManager.setReverseLayout(true);
       event_recyclerview.setLayoutManager(layoutManager);

        mAuth=FirebaseAuth.getInstance();
        CurrentClubID=mAuth.getCurrentUser().getUid();

       PostRef= FirebaseDatabase.getInstance().getReference().child("Club Posts").child(CurrentClubID);
       PostRef2= FirebaseDatabase.getInstance().getReference().child("Club Posts");


       create_event_layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(getContext(), EventsMangeActivity.class);
               startActivity(intent);

           }
       });




        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostItem> options=new FirebaseRecyclerOptions.Builder<PostItem>()
                .setQuery(PostRef,PostItem.class)
                .build();

        FirebaseRecyclerAdapter<PostItem,EventsViewHolder> adapter=new FirebaseRecyclerAdapter<PostItem, EventsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventsViewHolder holder, int position, @NonNull final PostItem model) {
                final String string=getRef(position).getKey();
                PostRef.child(string).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
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
                                    int commentCounter=0;
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


                            final String type=dataSnapshot.child("type").getValue().toString();
                            if (type.equals("event")){
                                final String event_name=dataSnapshot.child("event_name").getValue().toString();
                                final String time=dataSnapshot.child("time").getValue().toString();
                                final String date=dataSnapshot.child("date").getValue().toString();
                                final String post=dataSnapshot.child("post").getValue().toString();
                                final String poster_profile_picture=dataSnapshot.child("poster_profile_picture").getValue().toString();
                                final String poster_name=dataSnapshot.child("poster_name").getValue().toString();
                                holder.Event_Name.setVisibility(View.VISIBLE);
                                holder.Event_Name.setText(event_name);
                                holder.Event_Who_Register.setVisibility(View.VISIBLE);
                                holder.Event_Who_Register.setText("See who is register "+event_name);
                                holder.TimeAndDate.setText(time+"\t"+date);
                                holder.Post.setText(post);
                                holder.Poster_Name.setText(poster_name);

                                holder.Post_likes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",time+"\t"+date);
                                        intent.putExtra("type",type);
                                        intent.putExtra("event_name",event_name);

                                        startActivity(intent);
                                    }
                                });
                                holder.Post_Comments.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), LikeAndCommentActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("post",post);
                                        intent.putExtra("poster_name",poster_name);
                                        intent.putExtra("poster_profile_picture",poster_profile_picture);
                                        intent.putExtra("timeAndDate",time+"\t"+date);
                                        intent.putExtra("type",type);
                                        intent.putExtra("event_name",event_name);
                                        startActivity(intent);
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
                                holder.Event_Who_Register.setVisibility(View.VISIBLE);
                                holder.Event_Who_Register.setText("See who is register "+event_name);
                                holder.Event_Who_Register.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(), EventListActivity.class);
                                        intent.putExtra("parent_uid",parent_uid);
                                        intent.putExtra("child_uid",child_uid);
                                        intent.putExtra("event_name",event_name);
                                        startActivity(intent);
                                    }
                                });

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
                                holder.cardView.setVisibility(View.GONE);
                                holder.Poster_Name.setVisibility(View.GONE);
                                holder.Post.setVisibility(View.GONE);
                                holder.Post_Image.setVisibility(View.GONE);
                                holder.TimeAndDate.setVisibility(View.GONE);
                                holder.Post_likes.setVisibility(View.GONE);
                                holder.Post_Comments.setVisibility(View.GONE);
                                holder.Poster_Profile_Picture.setVisibility(View.GONE);
                                holder.likes_number.setVisibility(View.GONE);
                                holder.Comments_number.setVisibility(View.GONE);
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
            public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
                EventsViewHolder viewHolder=new EventsViewHolder(view);
                return viewHolder;
            }
        };
        event_recyclerview.setAdapter(adapter);
        adapter.startListening();

    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder{
        private TextView Poster_Name,Post,TimeAndDate,likes_number,Comments_number,Event_Name,Event_Who_Register,MoreActivity;
        private CircleImageView Poster_Profile_Picture;
        private ImageView Post_Image,Post_likes,Post_Comments;
        private CardView cardView;



        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardview);

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
            Event_Who_Register=itemView.findViewById(R.id.whoRegisteredEvent);
            MoreActivity=itemView.findViewById(R.id.moreActivity);

        }
        }
    }

