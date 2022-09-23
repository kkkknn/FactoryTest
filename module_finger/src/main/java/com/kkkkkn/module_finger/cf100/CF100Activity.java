package com.kkkkkn.module_finger.cf100;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zcfinger_util.ModuleTools;
import com.kkkkkn.module_finger.R;

import java.util.HashMap;
import java.util.Map;

public class CF100Activity extends AppCompatActivity implements View.OnClickListener{

    private Button open,close;
    private ImageView imageView;
    private TextView text_value;
    private Context mContext;
    private PendingIntent mPermissionIntent;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mDeviceConnection;
    private UsbInterface mInterface;
    private ModuleTools moduleTools;
    private final String TAG="智创科技";
    private final int VID=1155,PID=22291;
    private boolean key=false;
    private Bitmap bitmap=null;
    private Handler handler;
    private HashMap<String,Object> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cf100);
        open=findViewById(R.id.open);
        close=findViewById(R.id.close);
        text_value=findViewById(R.id.text_value);
        imageView=findViewById(R.id.imageView);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        mContext=getApplicationContext();

        handler=new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==13){
                    imageView.setImageBitmap((Bitmap)msg.obj);
                }else if(msg.what==22){
                    String str="score："+msg.obj;
                    text_value.setText(str);
                }else if(msg.what==201){
                    Toast.makeText(mContext,"close connection sucess",Toast.LENGTH_SHORT).show();
                    //取消按钮不可选中
                    open.setEnabled(true);
                    close.setEnabled(true);

                }else if(msg.what==202){
                    //取消按钮不可选中
                    open.setEnabled(true);
                    close.setEnabled(true);
                }
            }
        };

        Open_dev();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.open) {
            Open_dev();
        } else if (id == R.id.close) {//调用关闭连接方法
            Close_Dev();
        }
    }
    /**
     * 开始设备====采集指纹数据前的准备
     */
    public void Open_dev(){
        if(mDeviceConnection==null&&moduleTools==null){
            //设置按钮为不可选中
            open.setEnabled(false);
            close.setEnabled(false);
            //调用开启连接方法==获取mDeviceConnection   mInterface对象
            boolean conn_key=GetConnection();
            if(conn_key){
                //创建jar包工具类ModuleTools对象
                moduleTools=new ModuleTools(mDeviceConnection,mInterface);
                boolean aa=moduleTools.Open_Dev();
                Log.i(TAG, ": 连接usb成功");
                if(aa){
                    Log.i(TAG, ": 连接成功");

                    key=true;
                    //调用持续采集的线程
                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(key){
                                search_data();
                            }//关闭设备
                            boolean close_key=moduleTools.Close_Dev();
                            if(close_key){
                                //关闭moduleTools对象
                                moduleTools=null;
                                //关闭连接对象
                                mDeviceConnection.close();
                                mDeviceConnection=null;
                                Log.i(TAG, ":关闭连接成功");
                                //handler 通知关闭成功
                                Message msg = handler.obtainMessage();
                                msg.what = 201;
                                handler.sendMessage(msg);// 发送消息

                            }else{
                                //handler 通知关闭失败
                                Message msg = handler.obtainMessage();
                                msg.what = 202;
                                handler.sendMessage(msg);// 发送消息
                                Log.i(TAG, ":关闭连接失败");
                            }
                        }
                    });
                    thread.start();
                }else{
                    Log.i(TAG, ": 连接失败");
                }
            }else{
                Log.i(TAG, ":工具类初始化失败");
            }
            //取消按钮为不可选中
            open.setEnabled(true);
            close.setEnabled(true);
        }else{
            Toast.makeText(mContext,"do not repeat connections",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  关闭设备及连接==
     */
    public void Close_Dev(){
        if(mDeviceConnection!=null&&moduleTools!=null){
            //设置按钮为不可选中
            open.setEnabled(false);
            close.setEnabled(false);
            //关闭采集线程
            key=false;
        }else{
            Toast.makeText(mContext,"Not connected , unable to disconnect.",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 采集指纹方法==返回object对象，需要强转成bitmap对象
     */
    private void search_data(){
        hashMap=moduleTools.Collection_Data();

        if(hashMap!=null){

            for(Map.Entry<String,Object> str : hashMap.entrySet()){
                if("指纹图像".equals(str.getKey())){
                    //bitmap新的一帧已经生成，通知主线程更新系统UI界面
                    Message msg = handler.obtainMessage();
                    bitmap=(Bitmap) str.getValue();
                    msg.obj = bitmap;// 只是起到一个标记的作用
                    msg.what = 13;
                    handler.sendMessage(msg);// 发送消息
                }
                if("图像分数".equals(str.getKey())){
                    //bitmap新的一帧已经生成，通知主线程更新系统UI界面
                    Message msg = handler.obtainMessage();
                    msg.obj = str.getValue();// 只是起到一个标记的作用
                    msg.what = 22;
                    handler.sendMessage(msg);// 发送消息
                }
            }
        }
    }

    /**
     * 获取USB连接对象方法
     */
    public boolean  GetConnection(){
        UsbManager usbManager=(UsbManager) getSystemService(USB_SERVICE);
        HashMap<String,UsbDevice> deviceList=usbManager.getDeviceList();
        if(!deviceList.isEmpty()){
            //遍历所有获取到的USB设备
            for(UsbDevice device:deviceList.values()){
                Log.i(TAG, "GetConnection: "+device.getVendorId()+"   "+device.getProductId());
                //比对VID、PID
                if (device.getVendorId()==VID&&device.getProductId()==PID){
                    for(int i=0;i<device.getInterfaceCount();i++){
                        //获取设备接口
                        UsbInterface intf=device.getInterface(i);
                        if(intf.getInterfaceClass()==255&&intf.getInterfaceSubclass()==255&&intf.getInterfaceProtocol()==255){
                            mUsbDevice=device;
                            if(usbManager.hasPermission(device)){
                                //打开设备，获取UsbDeviceConnection对象，连接设备，用于后面的通讯
                                mDeviceConnection=usbManager.openDevice(device);
                                if(mDeviceConnection.claimInterface(intf,true)){
                                    mInterface=intf;
                                    //获取out in 方向参数
                                    Toast.makeText(mContext,"Connection success!",Toast.LENGTH_SHORT).show();
                                    return true;
                                }else{
                                    mDeviceConnection.close();
                                    onDestroy();
                                }
                            }else {
                                //无权限===获取权限
                                //注册USB设备权限
                                IntentFilter filter = new IntentFilter("com.android.example.USB_PERMISSION");
                                registerReceiver(usbReceiver, filter);
                                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
                                usbManager.requestPermission(mUsbDevice, mPermissionIntent);
                                Log.i(TAG, "GetConnection: 无权限，申请USB权限");
                            }
                            break;
                        }
                    }
                }else{
                    Toast.makeText(mContext,"Not found device!",Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(mContext,"Not found device!",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //权限注册=广播
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.android.example.USB_PERMISSION".equals(action)) {
                synchronized (this) {
                    UsbDevice device =  intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Open_dev();
                        }
                    } else {
                        Toast.makeText(mContext,"USB permissions Error,this APP will exit！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onPause: 调用停止");
        Close_Dev();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

