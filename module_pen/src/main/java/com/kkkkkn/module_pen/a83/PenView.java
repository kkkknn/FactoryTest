package com.kkkkkn.module_pen.a83;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * 执行画方法 继承surfaceview
 */
public class PenView extends SurfaceView  implements SurfaceHolder.Callback{
    private final static String TAG="PenView";
    private final static float penwidth=1.3f;
    private float upx,upy,upz;
    private Paint paint=new Paint();
    private Path path=new Path();
    private int count,packCount;
    private Canvas canvas,canvas2;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap bitmapCache;
    private Thread CanvasDraw;
    private PaintFlagsDrawFilter paintFlagsDrawFilter=new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

    //笔轨迹、笔压力
    private ArrayList<Path> pen_paths=new ArrayList<>();
    private ArrayList<Float> pen_widths=new ArrayList<>();
    //线程控制
    private boolean drawing=false;
    public PenView(Context context) {
        super(context);
        //初始化
        initSurfaceView();

    }

    public PenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurfaceView();

    }

    public PenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurfaceView();
    }

    void initSurfaceView(){
        //初始化画笔
        paint.setColor(Color.BLACK);
        //画笔设置
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setStrokeWidth(penwidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

    }



    //移动事件
    public void penmove(float x,float y,float z){

        float zlx=(x+upx)/2;
        float zly=(y+upy)/2;

        float valupz=(z/128)*penwidth;
        //float valupz=((z/100)/4)*penwidth;
        //变化过大直接退出
        if((valupz-upz)>=3){
            valupz=upz+0.5f;
        }else if((upz-valupz)>=3){
            valupz=upz-0.5f;
        }


        float valx=Math.abs(x-upx);
        float valy=Math.abs(y-upy);
        if(valx>=5||valy>=5){
            path.quadTo(upx,upy,zlx,zly);
        }else{
            path.lineTo(zlx,zly);
        }
        //path.quadTo(upx,upy,zlx,zly);


        if(upz!=valupz){

            /*if(valupz>upz){
                upz=upz/0.95f;
            }else{
                upz=upz*0.95f;
            }*/
            upz=valupz;
            pen_widths.add(valupz);
            pen_paths.add(path);
            path=new Path();
            path.moveTo(zlx,zly);
            //paint.setStrokeWidth(upz);
        }
        upx=zlx;
        upy=zly;
    }

    //按下事件
    public void pendown(float x,float y,float z){
        path.moveTo(x,y);
        upx=x;
        upy=y;
        upz=(z/128)*penwidth;
    }

    //抬起事件
    public void penup(){
        //初始化画笔宽度
        //pen_widths.add(upz);
        //pen_widths.clear();
        //pen_paths.clear();
        // pen_paths.add(path);
    }

    //清理画布
    public  void clear(){
        //路径重置
        path.reset();
        pen_widths.clear();
        pen_paths.clear();
        packCount=0;
        bitmapCache=null;
    }

    //surfaceview 创建时
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启线程
        drawing=true;
        CanvasDraw=new Thread(){
            @Override
            public void run() {
                while (drawing){
                    long start_time = System.currentTimeMillis();
                    count=pen_paths.size();
                    draw_Penpath();
                    long end_time = System.currentTimeMillis();

                    long value_time=end_time - start_time;
                    if (value_time < 30) {
                        try {
                            Thread.sleep(30 - (value_time));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        CanvasDraw.start();
    }
    //surfaceview 发生改变时
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //surfaceview 销毁时
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //关闭线程
        drawing=false;
    }



    private synchronized void draw_Penpath(){
        try {
            //获得canvas对象
            canvas = mSurfaceHolder.lockCanvas();
            //Log.i(TAG, "draw_Penpath: "+rect.toString());
            //绘制背景
            //canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
            canvas.setDrawFilter(paintFlagsDrawFilter);
            if(bitmapCache==null){
                //bitmapCache = BitmapFactory.decodeResource(getResources(), R.drawable.show);
                bitmapCache = Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(), Bitmap.Config.ARGB_8888);
                if(canvas2==null){
                    canvas2=new Canvas(bitmapCache);
                    canvas2.setDrawFilter(paintFlagsDrawFilter);
                }else
                {
                    canvas2.setBitmap(bitmapCache);
                }
                canvas2.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmapCache,0,0,paint);
            }else{
                if(canvas2==null){
                    canvas2=new Canvas(bitmapCache);
                    canvas2.setDrawFilter(paintFlagsDrawFilter);
                }else
                {
                    canvas2.setBitmap(bitmapCache);
                }
                //绘制路径  多重路径
                if(count==0){
                }else{
                    //long start_time = System.currentTimeMillis();
                    while(packCount<count){
                        paint.setStrokeWidth(pen_widths.get(packCount));
                        canvas2.drawPath(pen_paths.get(packCount), paint);
                        packCount++;
                    }
                    //long end_time = System.currentTimeMillis();
                    //Log.i(TAG, "draw_Penpath:耗时"+(end_time-start_time));
                    //pen_widths.clear();
                    //pen_paths.clear();
                    if(!path.isEmpty()){
                        paint.setStrokeWidth(upz);
                        canvas2.drawPath(path, paint);
                    }
                    //paint.setStrokeWidth(0);
                }

                canvas.drawBitmap(bitmapCache,0,0,paint);
            }


        }catch (Exception e){
            Log.i(TAG, e.toString());
        }finally {
            if (canvas != null){
                //释放canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
