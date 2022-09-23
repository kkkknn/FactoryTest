package com.edata;

/**
 * 签名坐标点
 * x，y坐标对应的坐标系为签名所用view的坐标系，以view左上角坐标为原点
 *
 */

public class Point {
    /**
     * 签名坐标点状态. 取值：FLOATING 0，PENDOWN 1，PENUP 2， PENMOVE 3
     *
     */
    public int mState;
    /**
     * 签名坐标点x值.取值范围0到view的宽度
     */
    public float mX;
    /**
     * 签字坐标点y值.取值范围0到view的高度
     */
    public float mY;
    /**
     * 签名坐标点压力值.0-2048
     */
    public float mZ;
}
