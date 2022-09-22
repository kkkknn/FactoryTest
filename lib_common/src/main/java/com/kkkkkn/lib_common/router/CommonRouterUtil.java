package com.kkkkkn.lib_common.router;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;

public class CommonRouterUtil {
    /**
     * 跳转到新页面
     * @param path 路径
     * @param context 对象
     */
    public static void navigationActivity(String path,
                                          Activity context) {
        ARouter.getInstance().build(path).navigation(context);
    }
}
