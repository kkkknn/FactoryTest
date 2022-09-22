package com.kkkkkn.lib_common.util;

public class OnClickUtil {
    /**
     * 最小允许间隔，低于则无法生效
     */
    private static final long INTERVAL = 500;

    /**
     * 上次点击时间点
     */
    private static long lastTime;

    /**
     * 是否点击过快
     *
     * @return
     */
    public static boolean isTooFast() {
        long nowTime = System.currentTimeMillis();
        long interval = nowTime - lastTime;
        if (interval > 0 && interval < INTERVAL) {
            return true;
        }
        lastTime = System.currentTimeMillis();
        return false;
    }

    public static boolean isTooFast(long INTERVAL) {
        long nowTime = System.currentTimeMillis();
        long interval = nowTime - lastTime;
        if (interval > 0 && interval < INTERVAL) {
            return true;
        }
        lastTime = System.currentTimeMillis();
        return false;
    }

    public static boolean isTooFast(int OWN_INTERVAL) {
        long nowTime = System.currentTimeMillis();
        long interval = nowTime - lastTime;
        if (interval > 0 && interval < OWN_INTERVAL) {
            return true;
        }
        lastTime = System.currentTimeMillis();
        return false;
    }

}


