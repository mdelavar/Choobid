package com.rayanehsabz.choobid.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sabz3 on 07/19/2017.
 */
public class BroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, NotiService.class);
        context.startService(myIntent);

    }
}