package com.example.adminofcubicle.Fragment.FragmentOfProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adminofcubicle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutFragment extends Fragment {
    private TextView Profile_Mission,Profile_Email,Profile_Website,Profile_Founded,Profile_Phone;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CardView email_card,website_card,phone_card,founded_card_1,founded_card_2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_about, container, false);
        Profile_Mission=view.findViewById(R.id.profile_mission);
        Profile_Email=view.findViewById(R.id.profile_email);
        Profile_Website=view.findViewById(R.id.profile_website);
        Profile_Founded=view.findViewById(R.id.profile_founded);
        Profile_Phone=view.findViewById(R.id.profile_phone);

        email_card=view.findViewById(R.id.email_card_view);
        website_card=view.findViewById(R.id.Web_card_view);
        phone_card=view.findViewById(R.id.call_card_view);
        founded_card_1=view.findViewById(R.id.Founed_card_view_1);
        founded_card_2=view.findViewById(R.id.Founed_card_view_2);

        RootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        RetrieveProfileInformation();

        return view;
    }

    private void RetrieveProfileInformation()
    {
        RootRef.child("Club Details").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String mission=dataSnapshot.child("mission").getValue().toString();
               String email=dataSnapshot.child("email").getValue().toString();
               String website=dataSnapshot.child("website").getValue().toString();
               String phone=dataSnapshot.child("phone").getValue().toString();
               String founded=dataSnapshot.child("founded").getValue().toString();
               Profile_Mission.setText(mission);

               if (!email.equals(""))
               {
                   Profile_Email.setVisibility(View.VISIBLE);
                   Profile_Email.setText(email);
                   email_card.setVisibility(View.VISIBLE);
               }
               if (!website.equals(""))
               {
                    Profile_Website.setVisibility(View.VISIBLE);
                    Profile_Website.setText(website);
                    website_card.setVisibility(View.VISIBLE);
               }
               if (!phone.equals(""))
               {
                    Profile_Phone.setVisibility(View.VISIBLE);
                    Profile_Phone.setText(phone);
                    phone_card.setVisibility(View.VISIBLE);
               }
               if (!founded.equals(""))
               {
                    Profile_Founded.setVisibility(View.VISIBLE);
                    founded_card_1.setVisibility(View.VISIBLE);
                    founded_card_2.setVisibility(View.VISIBLE);
                    Profile_Founded.setText(founded);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
