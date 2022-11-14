package com.kkkkkn.module_wifi.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kkkkkn.module_wifi.R;
import com.kkkkkn.module_wifi.bluetooth.util.BluetoothUtil;
import com.kkkkkn.module_wifi.bluetooth.view.BluetoothCustomAdapter;
import com.kkkkkn.module_wifi.bluetooth.view.BluetoothViewItem;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BluetoothFragment extends Fragment {
    public static final String TAG = "BluetoothFragment";
    private BluetoothCustomAdapter bluetoothAdapter;
    private List<BluetoothViewItem> bluetoothViewItems = new ArrayList<>();
    private SwitchButton switchButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BluetoothUtil bluetoothUtil;
    private boolean isSupportBluetooth=false;
    private boolean isOpenBluetooth=false;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        //搜索到蓝牙设备了
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        try {
                            BluetoothViewItem item=new BluetoothViewItem();
                            String name=device.getName();
                            if(name==null){
                                name=device.getAddress();
                            }
                            item.setName(name);
                            int position=bluetoothViewItems.size();
                            bluetoothViewItems.add(item);
                            bluetoothAdapter.notifyItemInserted(position);
                        }catch (SecurityException e){
                            e.printStackTrace();
                        }
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.d(TAG, "蓝牙正在打开");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.d(TAG, "蓝牙已经打开");
                                isOpenBluetooth=true;
                                if(!bluetoothUtil.searchDevice(requireContext())){
                                    Log.i(TAG, "onReceive: 搜索失败");   
                                }
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.d(TAG, "蓝牙正在关闭");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                Log.d(TAG, "蓝牙已经关闭");
                                isOpenBluetooth=false;
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        Log.d(TAG, "蓝牙设备已连接");
                        //.getInstance(context).updateBluetoothlist();
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        Log.d(TAG, "蓝牙设备已断开");
                        // CacheArrayManager.getInstance(HwContext.getContext()).disConnectGatt("val");
                        //CacheArrayManager.getInstance(context).updateBluetoothlist();
                        break;
                    case BluetoothDevice.ACTION_ALIAS_CHANGED:
                        Log.d(TAG, "蓝牙设备名称改变");
                        //CacheArrayManager.getInstance(context).updateBluetoothlist();
                        break;
                }
            }
        }
    };

    public BluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);


        requireActivity().registerReceiver(broadcastReceiver, intentFilter);//注册广播

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        bluetoothUtil=BluetoothUtil.getInstance();

        isSupportBluetooth=bluetoothUtil.isSupportBluetooth(requireContext());

        //设置初始状态
        isOpenBluetooth=bluetoothUtil.isBluetoothOpen();
        if(isOpenBluetooth){
            //获取已配对设备
            List<String> names=bluetoothUtil.getPairedDevices(requireContext());
            for (String str:names) {
                BluetoothViewItem item=new BluetoothViewItem();
                item.setName(str);
                int position=bluetoothViewItems.size();
                bluetoothViewItems.add(item);
                bluetoothAdapter.notifyItemInserted(position);
            }

            bluetoothUtil.searchDevice(requireContext());
        }
        switchButton.setChecked(isOpenBluetooth);

    }

    private void initView(View view) {
        switchButton=view.findViewById(R.id.switch_button);
        swipeRefreshLayout=view.findViewById(R.id.refresh);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_700, R.color.flush_color, R.color.purple_700);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(!isSupportBluetooth||!isOpenBluetooth){
                Log.i(TAG, "onRefresh: 没打开或者不支持蓝牙");
                return;
            }
            new Handler().postDelayed(() -> {
                bluetoothViewItems.clear();
                bluetoothAdapter.notifyItemRangeRemoved(0,bluetoothViewItems.size());

                //获取已配对设备
                List<String> names=bluetoothUtil.getPairedDevices(requireContext());
                for (String str:names) {
                    BluetoothViewItem item=new BluetoothViewItem();
                    item.setName(str);
                    int position=bluetoothViewItems.size();
                    bluetoothViewItems.add(item);
                    bluetoothAdapter.notifyItemInserted(position);
                }
                bluetoothUtil.searchDevice(requireContext());

                swipeRefreshLayout.setRefreshing(false);
            }, 1000);  //延迟1秒执行
        });

        switchButton.setOnCheckedChangeListener((view1, isChecked) -> {
            if(!isSupportBluetooth){
                return;
            }
            if(!XXPermissions.isGranted(requireContext(),Manifest.permission.BLUETOOTH_CONNECT)){
                Log.i(TAG, " 无权限返回失败");
                return;
            }
            if (isChecked) {
                if(!bluetoothUtil.openBluetooth()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchButton.setChecked(false);
                        }
                    }, 500);
                }
            } else {
                if(!bluetoothUtil.closeBluetooth()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchButton.setChecked(true);
                        }
                    }, 500);
                }else {
                    bluetoothAdapter.notifyItemRangeRemoved(0,bluetoothViewItems.size());
                    bluetoothViewItems.clear();
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.bluetooth_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        bluetoothAdapter = new BluetoothCustomAdapter(bluetoothViewItems);
        recyclerView.setAdapter(bluetoothAdapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(broadcastReceiver);
    }
}