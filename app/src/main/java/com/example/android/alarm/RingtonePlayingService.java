package com.example.android.alarm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;

public class RingtonePlayingService extends Service {

    private static final int REQUESTCODE = 777;

    Context context;
    int startId;
    boolean isRunning;
    NotificationManager notify_manager;

    MediaPlayer media_song;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this;
        Log.i("LocalService", "Received start id " + startId + ": " + intent);


        String state = intent.getExtras().getString("extra");

        Log.e("ringtone extra is", state);



        // Converts the extra strings from intent to startIds, 0 or 1

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                Log.e("start id is", Integer.toString(startId));

                break;
            case "alarm off":
                startId = 0;
                Log.e("start id is", Integer.toString(startId));
                break;
            default:
                startId = 0;
                break;
        }

        // if else statements

        // no music + alarm on is pressed -> music should play

        if (!this.isRunning && startId == 1) {

            Log.e("no music", "want on");

            // Intent for notification banner to stop alarm
            Intent stopAlarmIntent = new Intent(this.getApplicationContext(), RingtonePlayingService.class);


            PendingIntent stopAlarmPendingIntent = PendingIntent.getActivity(this, 0,
                    stopAlarmIntent, 0);


            // creating an instance of the media player
            media_song = MediaPlayer.create(this, R.raw.stranger_things);

            // start the ringtone
            media_song.start();

            this.isRunning = true;
            this.startId = 0;

            // notification
            // Setup notification service

            notify_manager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);

            // setup an intent that goes to the main activity

            Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);

            // setup pending intent

            PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                    intent_main_activity, 0);

            // make the notification parameters

            Intent receiverIntent = new Intent(this, AlarmFinishReceiver.class);
            PendingIntent pendingReceiverIntent = PendingIntent.getBroadcast(
                    this, REQUESTCODE, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification_popup = new Notification.Builder(this)
                    .setContentTitle("An alarm is going off!")
                    .setContentText("Click me!")
                    .setContentIntent(pending_intent_main_activity)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .addAction(R.mipmap.ic_launcher, "Stop", pendingReceiverIntent)
                    .build();

            // setup notification call command

            notify_manager.notify(0, notification_popup);
        }

        // music + alarm off is pressed -> music should stop

        else if (this.isRunning && startId == 0) {

            Log.e("music", "want off");

            // stop the ringtone
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;

        }

        // no music + alarm off is pressed -> nothing

        else if (!this.isRunning) {

            Log.e("no music", "alarm off");

            this.isRunning = false;
            this.startId = 0;

        }

        // catch odd events (possible bugs)

        else {

            Log.e("How did this", "happen?");

            this.isRunning = true;
            this.startId = 1;

        }

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {

        notify_manager.cancel(0);

        media_song.stop();
        media_song.release();

        this.isRunning = false;

        // Tell the user we stopped.
        Toast.makeText(this, "on Destroy called", Toast.LENGTH_SHORT).show();
    }

}
