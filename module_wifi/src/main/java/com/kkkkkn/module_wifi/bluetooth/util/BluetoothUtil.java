package com.kkkkkn.module_wifi.bluetooth.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

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
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter != null;
    }

    /**
     * 开启蓝牙设备
     * @param context
     * @return
     */
    public boolean openBluetooth(Context context) {
        if (bluetoothAdapter == null) {
            Log.i(TAG, "openBluetooth: bluetoothAdapter == null");
            return false;
        }
        // 打开蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //无权限返回失败
                Log.i(TAG, "openBluetooth: !bluetoothAdapter.isEnabled()  无权限返回失败");
                return false;
            }
            return bluetoothAdapter.enable();
        }
        Log.i(TAG, "openBluetooth: true");
        return true;
    }

    public boolean closeBluetooth(Context context) {
        if (bluetoothAdapter == null) {
            Log.i(TAG, "closeBluetooth: bluetoothAdapter == null");
            return false;
        }
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                
                return false;
            }
            return bluetoothAdapter.disable();
        }
        Log.i(TAG, "closeBluetooth: true");
        return true;
    }

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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 检查权限
            return false;
        }
        bluetoothAdapter.startDiscovery();
        return true;
    }


}
