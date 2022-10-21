package com.kkkkkn.module_wifi.wifi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.utils.TextUtils;
import com.kkkkkn.module_wifi.R;
import com.kkkkkn.module_wifi.wifi.bean.WifiConfig;
import com.kkkkkn.module_wifi.wifi.util.MacUtil;
import com.kkkkkn.module_wifi.wifi.util.WifiControlUtil;
import com.kkkkkn.module_wifi.wifi.view.DialogConnWifi;
import com.kkkkkn.module_wifi.wifi.view.WifiAdapter;
import com.kkkkkn.module_wifi.wifi.view.WifiViewItem;
import com.suke.widget.SwitchButton;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiFragment extends Fragment {
    public static final String TAG = "WifiFragment";
    private WifiAdapter wifiAdapter;
    private SwitchButton switchButton;
    private WifiControlUtil wifiControlUtil;
    private AppCompatTextView wifi_state_ip;
    private AppCompatTextView wifi_state_name;
    private AppCompatTextView device_mac_name;
    private List<WifiViewItem> wifiViewItemList = new ArrayList<>();
    private DialogConnWifi dialogConnWifi;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    Log.i("H3c", "isConnected" + isConnected);
                    if (isConnected) {
                        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if (wifiInfo != null) {
                            wifi_state_ip.setVisibility(View.VISIBLE);
                            wifi_state_name.setVisibility(View.VISIBLE);
                            wifi_state_ip.setText(ip2str(wifiInfo.getIpAddress()));
                            wifi_state_name.setText("SSID：" + wifiInfo.getSSID());
                        }
                        Log.i(TAG, "onReceive: " + wifiInfo.getSSID());
                        Log.i(TAG, "onReceive: " + ip2str(wifiInfo.getIpAddress()));
                        //开始搜索列表
                        if (progressDialog != null && progressDialog.isShowing()) {
                            dismissLoadDialog();
                        } else {
                            syncWifiList();
                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initView(view);
        initData();


    }

    private void initView(View view) {
        wifi_state_ip = view.findViewById(R.id.wifi_state_connIp);
        wifi_state_name = view.findViewById(R.id.wifi_state_connName);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        switchButton = view.findViewById(R.id.switch_button);
        device_mac_name=view.findViewById(R.id.device_mac);

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_700, R.color.flush_color, R.color.purple_700);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //开始刷新

                wifiViewItemList.clear();
                wifiAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //同步加载网络数据
                        syncWifiList();
                        wifiAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);  //延迟10秒执行
            }
        });

        switchButton.setOnCheckedChangeListener((view1, isChecked) -> {
            if (isChecked) {
                wifiControlUtil.openWifi();
            } else {
                wifiControlUtil.closeWifi();
                wifiViewItemList.clear();
                wifiAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wifi_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        wifiAdapter = new WifiAdapter(wifiViewItemList);
        recyclerView.setAdapter(wifiAdapter);
        wifiAdapter.setOnItemOnClickListener(new WifiAdapter.OnItemOnClickListener() {
            @Override
            public void onClick(int position) {
                WifiViewItem item = wifiViewItemList.get(position);
                Log.i(TAG, "onClick: 点击了" + item.getName());
                //弹窗并显示连接信息
                dialogConnWifi.setTitle(item.getName());
                dialogConnWifi.setWifiInfo(item.getName(), item.getSignalLevelStr(), item.getCapabilities());
                dialogConnWifi.setListener(new DialogConnWifi.Listener() {
                    @Override
                    public void onClick(WifiConfig wifiConfig) {
                        showLoadDialog();
                        wifiControlUtil.connWifi(wifiConfig, requireContext());
                        Log.i(TAG, "onClick: 哈哈，开始连接");
                    }
                });
                if (!dialogConnWifi.isShowing()) {
                    dialogConnWifi.show();
                }
            }
        });


    }

    private void initData() {

        wifiControlUtil = WifiControlUtil.getInstance();
        wifiControlUtil.setContext(requireContext());

        if (wifiControlUtil.isWifiOpen()) {
            switchButton.setChecked(true);
            syncWifiList();
        }

        dialogConnWifi = new DialogConnWifi(requireContext());

        String mac=MacUtil.getMacAddress(requireContext());
        if(!TextUtils.isEmpty(mac)){
            device_mac_name.setText(mac);
        }
    }

    private void syncWifiList() {
        ArrayList<ScanResult> scanResults = wifiControlUtil.searchWifi(requireContext());
        ArrayList<WifiViewItem> wifiViewItemArrayList = new ArrayList<>();
        for (ScanResult result : scanResults) {
            WifiViewItem wifiViewItem = new WifiViewItem();
            wifiViewItem.setName(result.SSID);
            wifiViewItem.setCapabilities(result.capabilities);

            int nSigLevel = WifiManager.calculateSignalLevel(
                    result.level, 5);
            wifiViewItem.setSignalLevel(nSigLevel);
            wifiViewItemArrayList.add(wifiViewItem);
        }
        //按照信号强度排序
        Collections.sort(wifiViewItemArrayList, new Comparator<WifiViewItem>() {
            @Override
            public int compare(WifiViewItem wifiViewItem, WifiViewItem t1) {
                if (wifiViewItem.getSignalLevel() < t1.getSignalLevel()) {
                    return 1;
                } else if (wifiViewItem.getSignalLevel() > t1.getSignalLevel()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        wifiViewItemList.clear();
        wifiViewItemList.addAll(wifiViewItemArrayList);
    }


    //IP地址转换
    private String ip2str(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 24) & 0xFF);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(broadcastReceiver);//取消注册广播
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册静态广播 监听wifi连接状态
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//添加监听网络改变的动作

        requireActivity().registerReceiver(broadcastReceiver, intentFilter);//注册广播
    }

    //显示加载框
    private void showLoadDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
        }
        progressDialog.setMessage("连接中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //隐藏加载框
    private void dismissLoadDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}