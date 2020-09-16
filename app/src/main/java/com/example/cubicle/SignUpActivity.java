package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private TextView AlreadyHaveAccountLink;
    private EditText UserEmail,UserPassword,UserName,StudentID;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference RoofRef;
    private GridView SignUp_GirdView;
    private ArrayList<String> ClubProfilePicture=new ArrayList<String>();
    private ArrayList<String> ClubProfileName=new ArrayList<String>();
    private DatabaseReference RootRef,RootRef2;
    ClubProfileGirdView clubProfileGirdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth=FirebaseAuth.getInstance();
        RoofRef= FirebaseDatabase.getInstance().getReference();



        IntiliazeFields();
        RootRef2= FirebaseDatabase.getInstance().getReference().child("Club Details");
        FetchDataFromDatabase();
        clubProfileGirdView=new ClubProfileGirdView(ClubProfilePicture,ClubProfileName);
        SignUp_GirdView.setAdapter(clubProfileGirdView);

        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }


    private void IntiliazeFields()
    {
        CreateAccountButton=findViewById(R.id.register_button);
        AlreadyHaveAccountLink=findViewById(R.id.already_have_account_link);
        UserEmail=findViewById(R.id.register_email);
        UserPassword=findViewById(R.id.register_password);
        UserName=findViewById(R.id.register_name);
        loadingBar=new ProgressDialog(this);
        StudentID=findViewById(R.id.Student_Id);
        SignUp_GirdView=findViewById(R.id.signUp_gridviewId);


    }

    private void CreateNewAccount()
    {
        final String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        final String name=UserName.getText().toString();
        final String studentId=StudentID.getText().toString();
        if (email.isEmpty()){
            Toast.makeText(this, "Require Email", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Require Password", Toast.LENGTH_SHORT).show();
        }
        else if (name.isEmpty()){
            Toast.makeText(this, "Require Student Name", Toast.LENGTH_SHORT).show();
        }
        else if (studentId.isEmpty()){
            Toast.makeText(this, "Require Student ID", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait...While we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser=mAuth.getCurrentUser();
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                    HashMap<String ,String> hashMap=new HashMap<>();
                                    hashMap.put("name",name);
                                    hashMap.put("id",studentId);
                                    hashMap.put("email",email);
                                    String currentUserID=mAuth.getCurrentUser().getUid();
                                    RoofRef.child("Student Details").child(currentUserID).setValue(hashMap);
                                    Toast.makeText(SignUpActivity.this, "Verification email has been sent.Please Verify your email account and Login here ..", Toast.LENGTH_SHORT).show();
                                    SendUserToLoginActivity();
                                    mAuth.signOut();
                                    loadingBar.dismiss();
                                    finish();



                            }
                        });

                    }
                    else {
                        String message=task.getException().toString();
                        Toast.makeText(SignUpActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }
    }

    private void SendUserToLoginActivity() {

        startActivity(new Intent(this,SignInActivity.class));
    }
    public class ClubProfileGirdView extends BaseAdapter
    {
        ArrayList<String> ClubProfilePicture2;
        ArrayList<String> ClubName2;

        public ClubProfileGirdView(ArrayList<String> clubProfilePicture2, ArrayList<String> clubName2) {
            ClubProfilePicture2 = clubProfilePicture2;
            ClubName2 = clubName2;
        }

        @Override
        public int getCount() {
            return this.ClubName2.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater=getLayoutInflater();
            View view=layoutInflater.inflate(R.layout.club_profile_girdview,null);
            final CircleImageView profilePicture=view.findViewById(R.id.clubGridViewProfilePicture);

            Picasso.get().load(ClubProfilePicture2.get(position)).networkPolicy(NetworkPolicy.OFFLINE).into(profilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(ClubProfilePicture2.get(position)).into(profilePicture);
                }
            });








            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SignUpActivity.this, ClubName2.get(position)+".Get more information please Sign up here", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

    }
    private void FetchDataFromDatabase() {
        RootRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1:snapshot.getChildren())
                {
                    if (dataSnapshot1.hasChild("name"))
                    {
                        ClubProfileName.add(dataSnapshot1.child("name").getValue().toString());
                    }
                    if (dataSnapshot1.hasChild("profile_picture"))
                    {
                        ClubProfilePicture.add(dataSnapshot1.child("profile_picture").getValue().toString());

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    }

