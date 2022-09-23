package com.kkkkkn.module_screen.touch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.kkkkkn.module_screen.R;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TouchActivity extends AppCompatActivity implements Runnable,OnTouchChangedListener {
    private static final String TAG = "TouchTest";
    private static final int MSG_BORDER_TOUCH = 1;
    private static final int MSG_CENTER_TOUCH = 2;
    private static final int MSG_END          = 3;

    private BorderTouchView mBorderView;
    private CenterTouchView mCenterView;

    private ScheduledThreadPoolExecutor mTimerTask;
    private int mIndex;

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            onHandleMessage(msg.arg1);
        }
    };


    private void onHandleMessage(final int index) {
        switch (index) {
            case MSG_BORDER_TOUCH:
                Log.i(TAG, "MSG_BORDER_TOUCH");
                mBorderView.setOnTouchChangedListener(this);
                mBorderView.setVisibility(View.VISIBLE);
                mCenterView.setVisibility(View.GONE);
                break;
            case MSG_CENTER_TOUCH:
                Log.i(TAG, "MSG_CENTER_TOUCH");
                mBorderView.setVisibility(View.GONE);
                mCenterView.setOnTouchChangedListener(this);
                mCenterView.setVisibility(View.VISIBLE);
                break;
            case MSG_END:
                Log.i(TAG, "MSG_END");
                //测试完成
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        initView();

        setTimerTask(MSG_BORDER_TOUCH, 0);
    }

    @Override
    public void run() {
        Message message = mHandler.obtainMessage();
        message.arg1 = mIndex;
        mHandler.sendMessage(message);
    }


    private void setTimerTask(final int index, int delay) {
        cancelTimerTask();

        mIndex = index;
        mTimerTask = new ScheduledThreadPoolExecutor(10);
        mTimerTask.schedule(this, delay, TimeUnit.MILLISECONDS);
    }


    private void initView() {
        mBorderView = (BorderTouchView) findViewById(R.id.touch_border);
        mCenterView = (CenterTouchView) findViewById(R.id.touch_center);
    }


    private void cancelTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.remove(this);
            mTimerTask.shutdownNow();
            mTimerTask = null;
        }
    }

    @Override
    public void onTouchFinish(View v) {
        if (v == mBorderView) {
            setTimerTask(MSG_CENTER_TOUCH, 1000);
        } else if (v == mCenterView) {
            setTimerTask(MSG_END, 1000);
        }
    }
}