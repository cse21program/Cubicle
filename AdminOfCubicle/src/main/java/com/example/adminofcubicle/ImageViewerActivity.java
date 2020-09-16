package com.example.adminofcubicle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        final ImageView imageViewer=findViewById(R.id.imageViewer);
        final String post=getIntent().getExtras().get("post").toString();
        Picasso.get().load(post).networkPolicy(NetworkPolicy.OFFLINE).into(imageViewer, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(post).into(imageViewer);
            }
        });
    }
}
