package com.kkkkkn.module_wifi.wifi.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.kkkkkn.module_wifi.R;

public class DialogConnWifi extends Dialog {

    public DialogConnWifi(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_conn_wifi);

        initView();

    }

    private void initView() {
        //设置弹窗大小
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.6);//设置dialog的宽度为当前手机屏幕的宽度*0.8
        p.height = (int)(size.y * 0.6);//设置dialog的高度为当前手机屏幕的高度*0.8
        getWindow().setAttributes(p);


    }
}
