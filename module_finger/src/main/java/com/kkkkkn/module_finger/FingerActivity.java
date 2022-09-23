package com.kkkkkn.module_finger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kkkkkn.lib_common.router.CommonRouterConstant;
import com.kkkkkn.lib_common.util.OnClickUtil;
import com.kkkkkn.module_finger.cf100.CF100Activity;
import com.kkkkkn.module_finger.cf200.CF200Activity;
import com.kkkkkn.module_finger.zk_fs200r.ZKActivity;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

@Route(path = CommonRouterConstant.FINGER)
public class FingerActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatButton btn_cf100,btn_cf200,btn_zk;
    private static final String TAG="FingerActivity";
    private static final int CF100_VID=0x0483;
    private static final int CF100_PID=0x5713;
    private static final int CF200A_VID=0x0483;
    private static final int CF200A_PID=0x5717;
    private static final int CF200B_VID=0x0483;
    private static final int CF200B_PID=0x5718;
    private static final int ZK_VID=6997;
    private static final int ZK_PID=772;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger);
        initView();

        //读取所有USB设备，判断VID PID,决定显示哪个设备
        boolean flag=GetConnection();
        if(!flag){
            //弹窗然后直接退出
            Toasty.error(this, "未找到指纹设备或不支持此指纹设备,程序退出", Toast.LENGTH_SHORT, true).show();
            finish();
        }
    }

    public boolean GetConnection(){
        UsbManager usbManager=(UsbManager) getSystemService(USB_SERVICE);
        HashMap<String, UsbDevice> deviceList=usbManager.getDeviceList();
        boolean isFind=false;
        if(!deviceList.isEmpty()){
            //遍历所有获取到的USB设备
            for(UsbDevice device:deviceList.values()){
                Log.i(TAG, "GetConnection: "+device.getVendorId()+"   "+device.getProductId());
                //比对VID、PID
                int vid=device.getVendorId();
                int pid=device.getProductId();
                if(vid==CF100_VID&&pid==CF100_PID){
                    btn_cf100.setVisibility(View.VISIBLE);
                    isFind=true;
                }else if((vid==CF200A_VID&&pid==CF200A_PID)||(vid==CF200B_VID&&pid==CF200B_PID)){
                    btn_cf200.setVisibility(View.VISIBLE);
                    isFind=true;
                }else if(vid==ZK_VID&&pid==ZK_PID){
                    btn_zk.setVisibility(View.VISIBLE);
                    isFind=true;
                }
            }
        }

        return isFind;
    }

    private void initView() {
        btn_cf100=findViewById(R.id.btn_cf100);
        btn_cf200=findViewById(R.id.btn_cf200);
        btn_zk=findViewById(R.id.btn_zk);

        btn_cf100.setOnClickListener(this);
        btn_cf200.setOnClickListener(this);
        btn_zk.setOnClickListener(this);

        btn_cf100.setVisibility(View.GONE);
        btn_cf200.setVisibility(View.GONE);
        btn_zk.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        if(OnClickUtil.isTooFast()){
            return;
        }
        int id=view.getId();
        Intent intent=null;
        if(id==R.id.btn_cf100){
            intent=new Intent(FingerActivity.this, CF100Activity.class);
        }else if(id==R.id.btn_cf200){
            intent=new Intent(FingerActivity.this, CF200Activity.class);
        }else if(id==R.id.btn_zk){
            intent=new Intent(FingerActivity.this, ZKActivity.class);

        }
        if(intent!=null){
            startActivity(intent);
        }

    }
}