package com.kkkkkn.module_wifi.bluetooth.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothUtil {
    public static final String TAG = "BluetoothUtil";
    private volatile static BluetoothUtil bluetoothUtil;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothUtil() {
    }

    public static BluetoothUtil getInstance() {
        if (bluetoothUtil == null) {
            synchronized (BluetoothUtil.class) {
                if (bluetoothUtil == null) {
                    bluetoothUtil = new BluetoothUtil();
                }
            }
        }
        return bluetoothUtil;
    }

    /**
     * 设备是否支持蓝牙
     * @return 是否支持蓝牙
     */
    public boolean isSupportBluetooth(Context context) {
        if(bluetoothAdapter==null){
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        return bluetoothAdapter != null;
    }

    /**
     * 开启蓝牙设备
     * @return
     */
    public boolean openBluetooth() {
        if (bluetoothAdapter == null) {
            Log.i(TAG, "openBluetooth: bluetoothAdapter == null");
            return false;
        }
        // 打开蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            boolean ret=false;
            try{
                ret=bluetoothAdapter.enable();
            }catch (SecurityException e){
                e.printStackTrace();
            }
            return ret;
        }
        Log.i(TAG, "openBluetooth: true");
        return true;
    }

    public boolean closeBluetooth() {
        if (bluetoothAdapter == null) {
            Log.i(TAG, "closeBluetooth: bluetoothAdapter == null");
            return false;
        }
        if (bluetoothAdapter.isEnabled()) {
            boolean ret=false;
            try {
                ret=bluetoothAdapter.disable();
            }catch (SecurityException e){
                e.printStackTrace();
            }
            return ret;
        }
        Log.i(TAG, "closeBluetooth: true");
        return true;
    }

    //获取之前已配对设备
    public ArrayList<String> getPairedDevices(Context context) {
        ArrayList<String> deviceNames=new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 检查权限
            return deviceNames;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (int i = 0; i < devices.size(); i++) {
            BluetoothDevice device = (BluetoothDevice) devices.iterator().next();
            deviceNames.add(device.getName());
        }
        return deviceNames;
    }

    /**
     * 搜索附近的蓝牙设备  需要上层实现蓝牙监听广播
     */
    public boolean searchDevice(Context context){
        if(!XXPermissions.isGranted(context,Manifest.permission.BLUETOOTH_CONNECT)){
            return false;
        }
        try {
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }catch (SecurityException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //是否已开启wifi
    public boolean isBluetoothOpen() {
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

}
