package com.example.cubicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cubicle.Fragment.FragmentOfDevelopers.JahedFragment;
import com.example.cubicle.Fragment.FragmentOfDevelopers.RezaulFragment;
import com.example.cubicle.Fragment.FragmentOfDevelopers.SupervisorFragment;
import com.example.cubicle.Fragment.FragmentOfDevelopers.TanvirFragment;

public class DevelopersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        Toolbar toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Developers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView=findViewById(R.id.listViewId);
        String[] Developers={"Supervisor","Rezaul Karim","Md Jahed Miah","Mahdi Hossan Tanvir"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Developers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment;
        if (position==0){
            fragment=new SupervisorFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentId,fragment).commit();
        }
        if (position==1){
            fragment=new RezaulFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentId,fragment).commit();
        }
        if (position==2){
            fragment=new JahedFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentId,fragment).commit();
        }
        if (position==3){
            fragment=new TanvirFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentId,fragment).commit();
        }
    }
}
