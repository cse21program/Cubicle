package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ClubProfileActivity extends AppCompatActivity
{
    private CircleImageView  Club_Profile_image;
    private TextView Club_Name,Club_Mission,Club_Phone,Club_Email,Club_Website,Club_Founded;
    private ImageView Club_Cover_Picture;
    Toolbar toolbar;
    private Button RequestBtn;
    private FirebaseAuth mAuth;
    private CardView Phone_card,Founded_card_1,Founded_card_2,Email_card,Website_card;
    private String visit_clubs_id,currentUserID,Current_state="new";
    private DatabaseReference ClubRef,MembershipRef,MemberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);
        ClubRef= FirebaseDatabase.getInstance().getReference().child("Club Details");
        MembershipRef= FirebaseDatabase.getInstance().getReference().child("Membership Request");
        MemberRef=FirebaseDatabase.getInstance().getReference().child("Club Members");
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
  });
        Club_Profile_image=findViewById(R.id.club_profile_image);
        Club_Cover_Picture=findViewById(R.id.club_cover_image);
        Club_Name=findViewById(R.id.club_name);
        Club_Mission=findViewById(R.id.club_mission);
        Club_Phone=findViewById(R.id.club_phone);
        Club_Email=findViewById(R.id.club_email);
        Club_Website=findViewById(R.id.club_web);
        Club_Founded=findViewById(R.id.club_founded);
        RequestBtn=findViewById(R.id.club_request_Btn);

        Phone_card=findViewById(R.id.phone_card);
        Founded_card_1=findViewById(R.id.founded_card1);
        Founded_card_2=findViewById(R.id.founded_card2);
        Email_card=findViewById(R.id.email_card);
        Website_card=findViewById(R.id.email_card);



        visit_clubs_id=getIntent().getExtras().get("visit_clubs_id").toString();

        RequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Current_state.equals("new"))
                {
                    SendMembershipRequest();
                }
                if (Current_state.equals("request_sent"))
                {
                    CancelMembershipRequest();
                }

            }
        });
        AlreadyMember();
        AlreadySendRequest();


        RetrieveClubsInformation();
    }

    private void AlreadySendRequest() {
        MembershipRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(visit_clubs_id)){
                    String request_type=dataSnapshot.child(visit_clubs_id).child("request_type").getValue().toString();
                    if (request_type.equals("sent"))
                    {
                       RequestBtn.setText("Cancel Membership Request");
                       Current_state="request_sent";
                    }
                    else {
                      Current_state="new";
                      RequestBtn.setText("Send Request for Membership");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AlreadyMember() {
        MemberRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(visit_clubs_id))
                {
                    String state=dataSnapshot.child(visit_clubs_id).child("Club Members").getValue().toString();
                    if (state.equals("Saved"))
                    {
                        Current_state="Club Members";
                        RequestBtn.setText("Already Member");
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RetrieveClubsInformation() {
        ClubRef.child(visit_clubs_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String mission=dataSnapshot.child("mission").getValue().toString();
                String email=dataSnapshot.child("email").getValue().toString();
                String phone=dataSnapshot.child("phone").getValue().toString();
                String website=dataSnapshot.child("website").getValue().toString();
                String founded=dataSnapshot.child("founded").getValue().toString();


                Club_Mission.setText(mission);
                Club_Name.setText(name);
                getSupportActionBar().setTitle(name);





                if (!email.equals("")){
                    Club_Email.setVisibility(View.VISIBLE);
                    Club_Email.setText(email);
                    Email_card.setVisibility(View.VISIBLE);
                }
                if (!phone.equals("")){
                    Club_Phone.setVisibility(View.VISIBLE);
                    Club_Phone.setText(phone);
                    Phone_card.setVisibility(View.VISIBLE);
                }
                if (!website.equals("")){
                    Club_Website.setVisibility(View.VISIBLE);
                    Club_Website.setText(website);
                    Website_card.setVisibility(View.VISIBLE);
                }
                if (!founded.equals("")){
                    Club_Founded.setVisibility(View.VISIBLE);
                    Club_Founded.setText(founded);
                    Founded_card_2.setVisibility(View.VISIBLE);
                    Founded_card_1.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.hasChild("cover_picture"))
                {
                    final String cover_picture=dataSnapshot.child("cover_picture").getValue().toString();

                    Picasso.get().load(cover_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Club_Cover_Picture, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(cover_picture).into(Club_Cover_Picture);
                        }
                    });
                }
                if (dataSnapshot.hasChild("profile_picture"))
                {
                    final String profile_picture=dataSnapshot.child("profile_picture").getValue().toString();

                    Picasso.get().load(profile_picture).networkPolicy(NetworkPolicy.OFFLINE).into(Club_Profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile_picture).into(Club_Profile_image);
                        }
                    });
                }


     }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void SendMembershipRequest()
    {

        MembershipRef.child(currentUserID).child(visit_clubs_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            RequestBtn.setText("Cancel Membership Request");
                            Current_state="request_sent";
                            MembershipRef.child(visit_clubs_id).child(currentUserID).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                RequestBtn.setText("Cancel Membership Request");
                                                Current_state="request_sent";

                                            }
                                            else {
                                                Current_state="new";
                                                RequestBtn.setText("Send Request for Membership");
                                            }

                                        }
                                    });
                        }
                        else {
                            Current_state="new";
                            RequestBtn.setText("Send Request for Membership");
                        }

                    }
                });

    }

    private void CancelMembershipRequest()
    {
        MembershipRef.child(currentUserID).child(visit_clubs_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Current_state="new";
                            RequestBtn.setText("Send Request for Membership");
                            MembershipRef.child(visit_clubs_id).child(currentUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Current_state="new";
                                                RequestBtn.setText("Send Request for Membership");

                                            }
                                            else {
                                                Current_state="request_sent";
                                                RequestBtn.setText("Cancel Membership Request");
                                            }
                                        }
                                    });
                        }
                        else {
                            Current_state="request_sent";
                            RequestBtn.setText("Cancel Membership Request");
                        }

                    }
                });
    }
}
