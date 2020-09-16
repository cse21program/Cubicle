package com.example.adminofcubicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.adminofcubicle.Fragment.MembersFragment;
import com.example.adminofcubicle.Fragment.NewsfeedFragment;
import com.example.adminofcubicle.Fragment.ProfileFragment;
import com.example.adminofcubicle.Fragment.RequestsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    private int[] tabIcons={R.drawable.newsfeed,R.drawable.profile,
            R.drawable.members,R.drawable.requests};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        RootRef= FirebaseDatabase.getInstance().getReference();

        viewPager=findViewById(R.id.view_paper_id);
        setUpViewpager(viewPager);

        tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabIcon();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser==null){
            SendUserPhoneAuthenticationActivity();
        }
        else {
            VerifyClubExistance();
        }
    }
    private void SendUserPhoneAuthenticationActivity() {
        Intent intent=new Intent(ProfileActivity.this,PhoneAuthenticationActivity.class);
        startActivity(intent);
    }

    private void setUpTabIcon() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setUpViewpager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new NewsfeedFragment(),"News feed");
        viewPagerAdapter.addFrag(new ProfileFragment(),"Profile");
        viewPagerAdapter.addFrag(new MembersFragment(),"Members");
        viewPagerAdapter.addFrag(new RequestsFragment(),"Requests");
        viewPager.setAdapter(viewPagerAdapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> fragments=new ArrayList<>();
        private final List<String> titles=new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFrag(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.layout_menu,menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.signout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),PhoneAuthenticationActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void VerifyClubExistance()
    {
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef.child("Club Details").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("name").exists()) && !(dataSnapshot.child("mission").exists())){
                    Toast.makeText(ProfileActivity.this, "Please set name and mission that's Mandatory", Toast.LENGTH_SHORT).show();
                    SendUserSettingActivity();



                }
                if (!(dataSnapshot.child("profile_picture").exists())){

                    SendUserSettingActivity();
                    Toast.makeText(ProfileActivity.this, "Please set your Profile Picture", Toast.LENGTH_SHORT).show();
                }
                if ((dataSnapshot.child("name").exists()) && (dataSnapshot.child("mission").exists())){

                    String name=dataSnapshot.child("name").getValue().toString();
                    getSupportActionBar().setTitle(name);


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
}
