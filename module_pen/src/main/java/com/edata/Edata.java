package com.edata;

import android.graphics.Bitmap;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * 智创签批板android SDK
 *
 */

public class Edata{
    /**
     * 清除签字内容命令ID
     */
    public static int CLEAR_DISPLAY = 83;
    /**
     * 准备签名环境
     * @return 1：成功，0：失败
     */
    public native int prepareForSign();
    /**
     * 结束签名，销毁签名环境
     *
     */
    public native void finishSign();
    /**
     * 执行签名操作。在prepareForSign后调用。需要在单独线程中循环调用。
     * 返回的有效Point集合可以用于回放函数replaySign
     * @return 无效点返回null，有效点返回Point对象。
     */
    public native Point doSign();
    /**
     * 回放签名过程
     * @param points 为doSign函数返回的有效点集合。需要在独立线程中调用。调用前须停止doSign所在线程
     *
     */
    public native void replaySign(Point[] points);
    /**
     *  恢复签名图像。在从后台切换至前台时从备份图像中恢复签名图像。
     *
     */
    public native void restoreSignView();
    /**
     * 备份签名图像。在从前台切换至后台时备份当前签名图像。
     *
     */
    public native void backupSignView();
    /**
     * 显示签名图像。在单独线程中循环调用。
     * @param surface 签名surfaceview的surface对象
     */
    public native void displaySign(Surface surface);
    /**
     * 发送命令
     * @param command 命令ID号。目前支持CLEAR_DISPLAY，清屏命令。
     */
    public native void doCommand(int command);
    /**
     * 初始化签字窗口参数。建议在surfaceChanged回调中调用。
     * @param left surfaceview的左上角x坐标
     * @param top surfaceview的左上角y坐标
     * @param width surfaceview的宽度（px）
     * @param height surfaceview的高度（px)
     * @param format 保留
     */
    public native void initSignView(int left, int top, int width, int height, int format);

    /**
     * 获取签名图像数据。签名图像大小与surfceview相同，16位色。
     *@return 返回签名图像数据buffer。未初始化时调用，返回null
     */
    public native byte[] getPixelBuffer();


    /**
     * 获取签名图像。签名图像大小与surfceview相同，16位色。
     * @param width 图像宽度，与surfaceview宽度相同
     * @param height 图像高度，与surfaceview高度相同
     *
     */
    public Bitmap getSignImage(int width, int height)
    {
        byte[] pb = getPixelBuffer();
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        if(null != pb){
            try{
                ByteBuffer buffer = ByteBuffer.wrap(pb);
                bm.copyPixelsFromBuffer(buffer);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return bm;
    }

    static {
        System.loadLibrary("edata");
    }
}









