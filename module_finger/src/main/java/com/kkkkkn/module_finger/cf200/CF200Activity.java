package com.kkkkkn.module_finger.cf200;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;


import com.kkkkkn.module_finger.R;
import com.zc.fingerutil_zc.ModuleListener;
import com.zc.fingerutil_zc.ZCFingerUtil;

import java.util.HashMap;

public class CF200Activity extends AppCompatActivity {
    private final static String TAG="MainActivity";
    private ZCFingerUtil zcFingerUtil;
    private ImageView imageView;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitch;
    private TextView textView,text_show;
    private Bitmap bitmap;
    private boolean isOpen=false;
    private final static int PID1 = 0x5717,VID1 = 0x0483;
    private final static int PID2 = 0x5718,VID2 = 0x0483;
    private final static int Module1_width=208;
    private final static int Module1_height=288;
    private final static int Module2_width=256;
    private final static int Module2_height=360;

    private final String USB_PERMISSION="com.android.example.USB_PERMISSION";


    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device =  intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if(device.getProductId()==PID1&&device.getVendorId()==VID1){
                        zcFingerUtil.initDevice(getApplicationContext(),Module1_width,Module1_height);
                    }else if(device.getProductId()==PID2&&device.getVendorId()==VID2){
                        zcFingerUtil.initDevice(getApplicationContext(),Module2_width,Module2_height);
                    }
                }
            }
        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case 2008:
                    textView.setText("指纹初始化成功");
                    String str=zcFingerUtil.getDeviceVersion();
                    text_show.setText("SDK版本号:"+zcFingerUtil.getSDKVersion()+"\n固件版本号："+str);
                    isOpen=true;
                    break;
                case 2009:
                    textView.setText("关闭指纹成功");
                    isOpen=false;
                    break;
                case 2010:
                    //开始调用函数展示指纹图像
                    float score= zcFingerUtil.getFingerScore();
                    if(bitmap!=null){
                        bitmap.recycle();
                        bitmap=null;
                    }
                    bitmap= zcFingerUtil.getFingerPrint();

                    imageView.setImageBitmap(bitmap);

                    textView.setText("score:"+score);
                    break;
                case -2000:
                    textView.setText("success: 连接失败");
                    break;
                case -2008:
                    textView.setText("success: 打开指纹失败");
                    aSwitch.setChecked(false);
                    break;
                case -2009:
                    textView.setText("success: 关闭指纹失败");
                    aSwitch.setChecked(true);
                    break;
                case -2010:
                    textView.setText("success: 采集指纹失败");
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cf200);
        //设置布局
        imageView=findViewById(R.id.fingerImg);
        imageView.setImageResource(R.drawable.show);
        textView=findViewById(R.id.text_finger);
        text_show=findViewById(R.id.text_show);
        //开始指纹采集
        aSwitch =findViewById(R.id.switch_finger);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    connectDevice();
                }else {
                    zcFingerUtil.releaseDevice();
                    text_show.setText("");
                }
            }
        });
        RadioGroup radioGroup_color=findViewById(R.id.radio_color);
        radioGroup_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(zcFingerUtil==null){
                    return;
                }
                if (i == R.id.color_red) {
                    zcFingerUtil.setFingerColor(0);
                } else if (i == R.id.color_gray) {
                    zcFingerUtil.setFingerColor(1);
                }
            }
        });
        radioGroup_color.check(R.id.color_red);

        RadioGroup radioGroup_mode=findViewById(R.id.radio_mode);
        radioGroup_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(zcFingerUtil==null){
                    return;
                }
                if (i == R.id.mode1) {
                    zcFingerUtil.setFingerMode(0);
                } else if (i == R.id.mode2) {
                    zcFingerUtil.setFingerMode(1);
                }
            }
        });
        radioGroup_mode.check(R.id.mode1);
        //开始指纹采集
        final ModuleListener listener=new ModuleListener() {
            @Override
            public void success(int successCode) {
                Message msg =Message.obtain();
                msg.what=successCode;
                handler.sendMessage(msg);
            }

            @Override
            public void error(int errorCode) {
                Message msg =Message.obtain();
                msg.what=errorCode;
                handler.sendMessage(msg);
            }
        };
        zcFingerUtil = ZCFingerUtil.getInstance(listener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
    }

    private void connectDevice() {
        UsbManager mUsbManager = (UsbManager) this.getSystemService(USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (!deviceList.isEmpty()) {
            //遍历所有获取到的USB设备
            for (UsbDevice device : deviceList.values()) {
                //比对VID、PID
                Log.i(TAG, "connectDevice: "+device.getVendorId()+"||"+device.getProductId());
                if(device.getProductId()==PID1&&device.getVendorId()==VID1){
                    if (mUsbManager.hasPermission(device)) {
                        zcFingerUtil.initDevice(getApplicationContext(),Module1_width,Module1_height);
                    } else {
                        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(USB_PERMISSION), 0);
                        mUsbManager.requestPermission(device, mPermissionIntent);
                    }
                }else if(device.getProductId()==PID2&&device.getVendorId()==VID2){
                    if (mUsbManager.hasPermission(device)) {
                        zcFingerUtil.initDevice(getApplicationContext(),Module2_width,Module2_height);
                    } else {
                        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(USB_PERMISSION), 0);
                        mUsbManager.requestPermission(device, mPermissionIntent);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(usbReceiver!=null){
            unregisterReceiver(usbReceiver);
        }
        if(isOpen){
            zcFingerUtil.releaseDevice();
        }
    }
}
