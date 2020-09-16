package com.example.cubicle;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.cubicle.Adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private TabAdapter tabAdapter;
    private int[] tabIcons={R.drawable.newsfeed,R.drawable.profile,
            R.drawable.member_of_clubes,R.drawable.activityofclubes};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar=findViewById(R.id.profile_tool_bar);
        setSupportActionBar(toolbar);

        RootRef= FirebaseDatabase.getInstance().getReference().child("Student Details");
        mAuth=FirebaseAuth.getInstance();


        viewPager=findViewById(R.id.student_view_pager);
        tabAdapter=new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        tabLayout=findViewById(R.id.students_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabIcon();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser==null){
            SendUserSignActivity();
        }
        else {
            VerifyUserExistance();
        }
    }

  private void VerifyUserExistance()
    {
        String currentUserID=mAuth.getCurrentUser().getUid();

        RootRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if ((dataSnapshot.hasChild("name"))){
                    String name=dataSnapshot.child("name").getValue().toString();
                    getSupportActionBar().setTitle(name);
                }

                if ((dataSnapshot.child("phone").exists())){

                }
                else {
                    SendUserSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserSettingActivity() {
        Intent intent=new Intent(ProfileActivity.this,SettingActivity.class);
        startActivity(intent);
    }

    private void SendUserSignActivity()
    {
        Intent intent=new Intent(ProfileActivity.this,SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_layout,menu);
        return true;
    }

    private void setUpTabIcon() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.developers:
                startActivity(new Intent(getApplicationContext(),DevelopersActivity.class));
                break;
            case R.id.feedback:
                startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                break;
            case R.id.share:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                String subject="Cubicle";
                String body="https://www.lus.ac.bd/";

                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT,body);

                startActivity(Intent.createChooser(intent,"Share with"));

                break;
            case R.id.signOutMenuId:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
