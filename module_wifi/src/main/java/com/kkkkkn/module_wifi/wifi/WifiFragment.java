package com.kkkkkn.module_wifi.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kkkkkn.module_wifi.R;
import com.kkkkkn.module_wifi.wifi.util.WifiControlUtil;
import com.kkkkkn.module_wifi.wifi.view.WifiAdapter;
import com.kkkkkn.module_wifi.wifi.view.WifiViewItem;
import com.suke.widget.SwitchButton;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WifiFragment extends Fragment {
    public static final String TAG="WifiFragment";
    private RecyclerView recyclerView;
    private SwitchButton switchButton;
    private WifiControlUtil wifiControlUtil;
    private AppCompatTextView wifi_state_ip;
    private AppCompatTextView wifi_state_name;
    private List<WifiViewItem> wifiViewItemList=new ArrayList<>();

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
                        if(wifiInfo!=null){
                            wifi_state_ip.setVisibility(View.VISIBLE);
                            wifi_state_name.setVisibility(View.VISIBLE);
                            wifi_state_ip.setText(ip2str(wifiInfo.getIpAddress()));
                            wifi_state_name.setText("SSID："+wifiInfo.getSSID());
                        }
                        Log.i(TAG, "onReceive: "+wifiInfo.getSSID());
                        Log.i(TAG, "onReceive: "+ip2str(wifiInfo.getIpAddress()));
                        //开始搜索列表
                        syncWifiList();
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
        wifi_state_ip=view.findViewById(R.id.wifi_state_connIp);
        wifi_state_name=view.findViewById(R.id.wifi_state_connName);

        recyclerView=view.findViewById(R.id.wifi_recyclerView);
        switchButton=view.findViewById(R.id.switch_button);

        switchButton.setOnCheckedChangeListener((view1, isChecked) -> {
            if(isChecked){
                wifiControlUtil.openWifi();
            }else {
                wifiControlUtil.closeWifi();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wifi_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        WifiAdapter adapter = new WifiAdapter(wifiViewItemList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemOnClickListener(new WifiAdapter.OnItemOnClickListener() {
            @Override
            public void onClick(int position) {
                Log.i(TAG, "onClick: 点击了"+wifiViewItemList.get(position).getName());
                //弹窗并显示连接信息

            }
        });


    }

    private void initData(){

        wifiControlUtil=new WifiControlUtil();
        wifiControlUtil.setContext(requireContext());

        if(wifiControlUtil.isWifiOpen()){
            switchButton.setChecked(true);
            syncWifiList();
        }
    }

    private void syncWifiList(){
        ArrayList<ScanResult> scanResults=wifiControlUtil.searchWifi();
        wifiViewItemList.clear();
        for (ScanResult result:scanResults) {
            WifiViewItem wifiViewItem=new WifiViewItem();
            wifiViewItem.setName(result.SSID);

            int nSigLevel = WifiManager.calculateSignalLevel(
                    result.level, 5);
            wifiViewItem.setSignalLevel(nSigLevel);
            wifiViewItemList.add(wifiViewItem);
        }
    }


    //IP地址转换
    private String ip2str(int ip){
        StringBuilder sb = new StringBuilder();
        sb.append(ip & 0xFF).append(".");
        sb.append((ip >> 8) & 0xFF).append(".");
        sb.append((ip >> 16) & 0xFF).append(".");
        sb.append((ip >> 24) & 0xFF);
        return "IP地址："+sb.toString();
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
}