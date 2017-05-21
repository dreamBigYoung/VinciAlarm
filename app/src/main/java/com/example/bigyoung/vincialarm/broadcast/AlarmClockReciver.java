package com.example.bigyoung.vincialarm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.bigyoung.vincialarm.MainActivity;
import com.example.bigyoung.vincialarm.activity.WakingUpActivity;
import com.example.bigyoung.vincialarm.utils.MyToast;

/**
 * Created by BigYoung on 2017/5/21.
 */

public class AlarmClockReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //MyToast.show(context,"hello world");
        Intent it=new Intent(context, WakingUpActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }
}
