package com.example.bigyoung.vincialarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.bigyoung.vincialarm.broadcast.AlarmClockReciver;
import com.example.bigyoung.vincialarm.utils.Constants;
import com.example.bigyoung.vincialarm.utils.MyToast;
import com.example.bigyoung.vincialarm.utils.PreferenceUtils;
import com.example.bigyoung.vincialarm.wrap.SimpleTextWatcher;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.working_duration)
    EditText mWorkingDuration;
    @BindView(R.id.resting_duration)
    EditText mRestingDuration;
    @BindView(R.id.alarm_switch)
    ImageButton mAlarmSwitch;
    private boolean mOpen_signal;//闹钟是否开启标识

    public static String CUR_CYCLE=Constants.WORKING_DURATION;//当前周期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        //进入界面，关闭闹钟
        closeClock();

        //获取服务是否已开启标识
        mOpen_signal = PreferenceUtils.getBoolean(MainActivity.this, Constants.ALARM_SIGNAL, false);
        //设置对应图标
        mAlarmSwitch.setImageResource(mOpen_signal ==false?R.drawable.off:R.drawable.on);
        //set latest saving value
        mRestingDuration.setText(PreferenceUtils.getString(MainActivity.this,Constants.REST_DURATION,20+""));
        mWorkingDuration.setText(PreferenceUtils.getString(MainActivity.this,Constants.WORKING_DURATION,2+""));
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mAlarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换状态
                mOpen_signal=!mOpen_signal;
                if(mOpen_signal==true){
                    openClock();
                }else{
                    closeClock();
                }
            }
        });
        //add text watcher
        mRestingDuration.addTextChangedListener(new MyTextWatcher(mRestingDuration,Constants.REST_DURATION));

        mWorkingDuration.addTextChangedListener(new MyTextWatcher(mWorkingDuration,Constants.WORKING_DURATION));

    }

    /**
     * 开启闹钟
     */
    private void openClock() {
        PreferenceUtils.setBoolean(MainActivity.this, Constants.ALARM_SIGNAL, mOpen_signal);
        //切换图片
        mAlarmSwitch.setImageResource(mOpen_signal ==false? R.drawable.off:R.drawable.on);
        //开启闹钟
        reStartClock(MainActivity.this);
    }
    /**
     * 关闭闹钟
     */
    private void closeClock() {
        //设置时间改变时，关闭闹钟
        mAlarmSwitch.setImageResource(R.drawable.off);
        mOpen_signal=false;
        //保存状态
        PreferenceUtils.setBoolean(MainActivity.this, Constants.ALARM_SIGNAL, mOpen_signal);
        cancelClock(MainActivity.this);
    }
    /**
     * text watcher
     */
    private class MyTextWatcher extends SimpleTextWatcher{
        EditText edit;//matched editview
        final String keyPerference;//key of sharedPreference

        public MyTextWatcher(EditText edit,final String keyPerference) {
            this.edit = edit;
            this.keyPerference = keyPerference;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            closeClock();
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            PreferenceUtils.setString(MainActivity.this,keyPerference,value);
        }
    }

    /**
     * 重新开启闹钟
     */
    public static void reStartClock(Context context){
        Intent intent = new Intent(context,
                AlarmClockReciver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, 0);
        // We want the alarm to go off saved minutes from now.
        //判断即将进入的时期
        int duration=0;
        switch (CUR_CYCLE){
            case Constants.REST_DURATION:
                duration=Integer.valueOf(PreferenceUtils.getString(context,Constants.REST_DURATION,2+""));
                sentClock(sender, duration,context);
                //设置下一个工作周期性质
                CUR_CYCLE=Constants.WORKING_DURATION;
                break;
            case Constants.WORKING_DURATION:
                duration=Integer.valueOf(PreferenceUtils.getString(context,Constants.WORKING_DURATION,20+""));
                sentClock(sender, duration,context);
                //设置下一个工作周期性质
                CUR_CYCLE=Constants.REST_DURATION;
                break;
            default:;
        }
    }

    /**
     * 撤销闹钟
     * @param context
     */
    public void cancelClock(Context context){
        Intent intent = new Intent(context,
                AlarmClockReciver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, 0);
        // And cancel the alarm.
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
    /**
     * 发送闹钟
     * @param sender
     * @param duration
     * @param context
     */
    private static void sentClock(PendingIntent sender, int duration,Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, duration);
        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyToast.show(MainActivity.this,"设置成功");
    }
}
