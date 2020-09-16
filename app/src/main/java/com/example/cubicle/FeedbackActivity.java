package com.example.cubicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

public class FeedbackActivity extends AppCompatActivity {
    private Toolbar fToolBar;
    private EditText fName,fDescription;
    private Button fSendBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        fToolBar=findViewById(R.id.feedbackToolBar);
        setSupportActionBar(fToolBar);
        getSupportActionBar().setTitle("Feedback Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fName=findViewById(R.id.feedbackName);
        fDescription=findViewById(R.id.feedbackDescription);
        fSendBtn=findViewById(R.id.FeedSendBtn);

        fSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=fName.getText().toString();
                String Description=fDescription.getText().toString();

                if (Name.isEmpty() && Description.isEmpty())
                {
                    Toast.makeText(FeedbackActivity.this, "All Field Require", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent(Intent.ACTION_SEND);

                    intent.setType("text/email");
                    intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"mohammad21program@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback From Cubicle");
                    intent.putExtra(Intent.EXTRA_TEXT,"Name:"+Name+"Feedback:"+Description);
                    startActivity(Intent.createChooser(intent,"Feedback With"));
                }
            }
        });


    }

}
