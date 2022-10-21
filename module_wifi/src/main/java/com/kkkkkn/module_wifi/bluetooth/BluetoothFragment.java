package com.kkkkkn.module_wifi.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
    private Handler handler;

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
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return ;
                        }
                        Log.d(TAG, device.getName());
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.d(TAG, "蓝牙正在打开");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.d(TAG, "蓝牙已经打开");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.d(TAG, "蓝牙正在关闭");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                Log.d(TAG, "蓝牙已经关闭");
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

        //注册静态广播 监听wifi连接状态
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);


        requireActivity().registerReceiver(broadcastReceiver, intentFilter);//注册广播

        Log.i(TAG, "checkPermission: 开始检查权限");
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.Group.BLUETOOTH)
                // 申请多个权限
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .permission(Permission.BLUETOOTH_CONNECT)
                .permission(Permission.BLUETOOTH_SCAN)
                .permission(Permission.BLUETOOTH_ADVERTISE)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            Log.i(TAG,"获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i(TAG,"获取录音和日历权限成功");
                        //toast("获取录音和日历权限成功");
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Log.i(TAG,"被永久拒绝授权，请手动授予录音和日历权限");
                            // toast("被永久拒绝授权，请手动授予录音和日历权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            //XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
                        } else {
                            Log.i(TAG,"获取录音和日历权限失败");
                            //toast("获取录音和日历权限失败");
                        }
                    }
                });
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
    }

    private void initView(View view) {
        switchButton=view.findViewById(R.id.switch_button);
        swipeRefreshLayout=view.findViewById(R.id.refresh);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_700, R.color.flush_color, R.color.purple_700);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isSupportBluetooth||!isOpenBluetooth){
                    return;
                }
                //开始刷新
                bluetoothViewItems.clear();
                bluetoothAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //同步加载网络数据
                        syncWifiList();
                        bluetoothAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);  //延迟10秒执行
            }
        });

        switchButton.setOnCheckedChangeListener((view1, isChecked) -> {
            if(!isSupportBluetooth){
                return;
            }
            if (isChecked) {
                if(!bluetoothUtil.openBluetooth(requireContext())){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchButton.setChecked(false);
                        }
                    }, 500);
                }
            } else {
                if(!bluetoothUtil.closeBluetooth(requireContext())){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchButton.setChecked(true);
                        }
                    }, 500);
                }else {
                    bluetoothViewItems.clear();
                    bluetoothAdapter.notifyDataSetChanged();
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.bluetooth_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        bluetoothAdapter = new BluetoothCustomAdapter(bluetoothViewItems);
        recyclerView.setAdapter(bluetoothAdapter);

    }

    //刷新蓝牙列表设备
    private void syncWifiList() {
        
    }

}