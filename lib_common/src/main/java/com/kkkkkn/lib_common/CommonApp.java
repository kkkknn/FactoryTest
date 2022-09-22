package com.kkkkkn.lib_common;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

public abstract class CommonApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (isDebug()) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

    }

    protected abstract boolean isDebug();
}
