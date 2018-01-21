package com.example.android.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by amirali on 2018-01-16.
 */

public class AlarmFinishReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
        context.stopService(serviceIntent);
    }

}