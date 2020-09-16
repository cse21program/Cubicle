package com.example.cubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.CENTER;

public class SignInActivity extends AppCompatActivity {
    private TextView NeedNewAccountLink,ForgetPassword;
    private EditText UserEmail,UserPassword;
    private Button LoginInButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private FirebaseUser currentUser;
    private GridView login_GirdView;
    private ArrayList<String> ClubProfilePicture=new ArrayList<String>();
    private ArrayList<String> ClubProfileName=new ArrayList<String>();
    private DatabaseReference RootRef;
    ClubProfileGirdView clubProfileGirdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        InitializeFields();
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        RootRef= FirebaseDatabase.getInstance().getReference().child("Club Details");
        FetchDataFromDatabase();
       clubProfileGirdView=new ClubProfileGirdView(ClubProfilePicture,ClubProfileName);
       login_GirdView.setAdapter(clubProfileGirdView);




        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
        LoginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowToUserLogin();
            }
        });
        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordMethod();
            }
        });

    }



    private void ForgetPasswordMethod() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Forget Password?");
        final EditText ForgetPasswordField=new EditText(SignInActivity.this);
        ForgetPasswordField.setHint("Enter Email");
        ForgetPasswordField.setGravity(CENTER);
        builder.setView(ForgetPasswordField);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String email=ForgetPasswordField.getText().toString();
                if (email.isEmpty())
                {
                    Toast.makeText(SignInActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }

                else {
                    loadingBar.setMessage("Verifying....");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SignInActivity.this, "Please check your email and Reset your password", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(SignInActivity.this, "Email don't exist"+task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser!=null){
            SendUserToProfileActivity();
        }
    }

    private void SendUserToRegisterActivity()
    {
        Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    private void AllowToUserLogin()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();

        if (email.isEmpty())
        {
            Toast.makeText(this, "Require All Field..", Toast.LENGTH_SHORT).show();
        }
        if (password.isEmpty())
        {
            Toast.makeText(this, "Require All Field..", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser=mAuth.getCurrentUser();

                        if (firebaseUser.isEmailVerified())
                        {
                            SendUserToProfileActivity();
                            Toast.makeText(SignInActivity.this, "Logged in Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                        else {
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                        loadingBar.dismiss();
                                        mAuth.signOut();
                                        Toast.makeText(SignInActivity.this, "Please Verify your email first", Toast.LENGTH_SHORT).show();


                                }
                            });

                        }

                    }
                    else {
                        String message=task.getException().toString();
                        Toast.makeText(SignInActivity.this, "Error :"+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }
    }

    private void SendUserToProfileActivity()

    {
        Intent intent=new Intent(SignInActivity.this,ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void InitializeFields()
    {
        UserEmail=findViewById(R.id.login_email);
        UserPassword=findViewById(R.id.login_password);
        NeedNewAccountLink=findViewById(R.id.need_new_account_link);
        LoginInButton=findViewById(R.id.login_button);
        loadingBar=new ProgressDialog(this);
        ForgetPassword=findViewById(R.id.forget_password_link);
        login_GirdView=findViewById(R.id.login_gridviewId);

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
                   Toast.makeText(SignInActivity.this, ClubName2.get(position)+"Get more information please Sign in here", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

    }
    private void FetchDataFromDatabase() {
        RootRef.addValueEventListener(new ValueEventListener() {
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

