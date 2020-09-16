package com.example.adminofcubicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rengwuxian.materialedittext.MaterialEditText;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText NameEditText,FeedbackEditText;
    private Button SendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feedback Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NameEditText=findViewById(R.id.nameId);
        FeedbackEditText=findViewById(R.id.descriptionId);

        SendButton=findViewById(R.id.sendButtonId);


        SendButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        String Name=NameEditText.getText().toString();
        String Feedback=FeedbackEditText.getText().toString();
        switch (v.getId()){
            case R.id.sendButtonId:
                    if (Name.isEmpty()){
                        NameEditText.setError("Please enter your name");
                        NameEditText.requestFocus();
                        return; }
                    if (Feedback.isEmpty()){
                        FeedbackEditText.setError("Please enter your feedback");
                        FeedbackEditText.requestFocus();
                        return; }
                    if(!Name.isEmpty() && !Feedback.isEmpty()){
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/email");
                        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"cse21program@gmail.com","mahdihossan98@gmail.com","mdjahedahmed666@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback from Cubicle");
                        intent.putExtra(Intent.EXTRA_TEXT,"Name:"+Name+"\n Feedback:"+Feedback);
                        startActivity(Intent.createChooser(intent,"Feedback from")); }
                        NameEditText.setText("");
                        FeedbackEditText.setText("");


                break;

        }

    }
}
