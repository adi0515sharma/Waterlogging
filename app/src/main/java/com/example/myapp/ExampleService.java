package com.example.myapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ExampleService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent backgroundIntent=new Intent(this,MyMap.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {


            PendingIntent background_pending=PendingIntent.getActivity(this,0,backgroundIntent,0);
            Notification background=new NotificationCompat.Builder(this,NotificationManager_.CHANNEL_3_ID)
                    .setSmallIcon(R.drawable.loginlogo)

                    .setContentTitle("Be Safe ")
                    .setContentText("Safety24x7 Prevent You Stay Safe")
                    .setContentIntent(background_pending)
                    .build();
            startForeground(1,background);


        }
        return START_NOT_STICKY;    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

