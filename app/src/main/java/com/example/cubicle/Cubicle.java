package com.example.cubicle;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class Cubicle  extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso build=builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);
    }


}
