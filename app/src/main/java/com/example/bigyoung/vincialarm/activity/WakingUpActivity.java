package com.example.bigyoung.vincialarm.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.bigyoung.vincialarm.MainActivity;
import com.example.bigyoung.vincialarm.R;
import com.example.bigyoung.vincialarm.utils.VibratorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BigYoung on 2017/5/21.
 */

public class WakingUpActivity extends Activity {
    @BindView(R.id.click_button)
    Button mClickButton;
    private long mShort;
    private long mLong;
    private PowerManager.WakeLock mWakelock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //锁屏状态下也可以显示
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
/*        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
        keyguardLock.disableKeyguard();*/
        setContentView(R.layout.activity_waking_up);
        ButterKnife.bind(this);

        //开启震动
        mShort = 200L;
        mLong = 2000L;
        long[] timePartter={mShort, mLong, mShort,mLong};
        VibratorUtil.Vibrate(timePartter,true);
    }

    @OnClick(R.id.click_button)
    public void onViewClicked() {
        this.finish();
    }

    private void prepareToNext() {
        //取消震动
        VibratorUtil.cancelVibrate();
        //准备进入下一周期
        MainActivity.reStartClock(WakingUpActivity.this);

        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //使button可用
        mClickButton.setEnabled(true);
        // 唤醒屏幕
        //acquireWakeLock();
    }
    /**
     * 唤醒屏幕
     */
    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //releaseWakeLock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prepareToNext();
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }
}
