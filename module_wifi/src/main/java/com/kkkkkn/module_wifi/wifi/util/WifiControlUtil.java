package com.kkkkkn.module_wifi.wifi.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.kkkkkn.module_wifi.wifi.bean.WifiConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//单例模式
public class WifiControlUtil {
    private volatile static WifiControlUtil wifiControlUtil;
    private WifiManager mWifiManager;


    public void setContext(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    //开启WiFi
    public void openWifi() {
        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //是否已开启wifi
    public boolean isWifiOpen() {
        if (mWifiManager != null) {
            return mWifiManager.isWifiEnabled();
        }
        return false;
    }

    //关闭WiFi
    public void closeWifi() {
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //获取WiFi列表
    public ArrayList<ScanResult> searchWifi(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        ArrayList<ScanResult> resultList = new ArrayList<>();
        if (mWifiManager != null && isWifiOpen()) {
            resultList.addAll(mWifiManager.getScanResults());
        }
        return resultList;
    }

    //连接WiFi
    public void connWifi(WifiConfig wifiConfig,Context context) {
        if(wifiConfig==null){
            return;
        }
        if(wifiConfig.connType== WifiConfig.CONNECT_TYPE.DHCP){
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            int netId = mWifiManager.addNetwork(getWifiConfig(wifiConfig.name, wifiConfig.pwd, wifiConfig.pwdType,context));
            mWifiManager.enableNetwork(netId, true);
        }else if(wifiConfig.connType==WifiConfig.CONNECT_TYPE.STATIC){
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            int netId = 0;
            try {
                InetAddress[] addresses=new InetAddress[1];
                addresses[0]=InetAddress.getByName(wifiConfig.staticConnConfig.dns_ip);
                netId = mWifiManager.addNetwork(setStaticIpConfiguration(
                        getWifiConfig(wifiConfig.name, wifiConfig.pwd, wifiConfig.pwdType,context),
                        InetAddress.getByName(wifiConfig.staticConnConfig.this_ip),
                        24,
                        InetAddress.getByName(wifiConfig.staticConnConfig.gateWay_ip),
                        addresses));
                Log.i("123", "connWifi: lalal");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            mWifiManager.enableNetwork(netId, true);
        }

        //重新连接wifi
        //mWifiManager.reconnect();
    }

    //断开当前连接WiFi
    public void disConnWifi() {

    }


    //IP地址转换
    public static String ip2str(int ip){
        StringBuilder sb = new StringBuilder();
        sb.append(ip & 0xFF).append(".");
        sb.append((ip >> 8) & 0xFF).append(".");
        sb.append((ip >> 16) & 0xFF).append(".");
        sb.append((ip >> 24) & 0xFF);
        return "IP地址："+sb.toString();
    }


    private WifiConfiguration getWifiConfig(String ssid, String pws, WifiConfig.PWD_TYPE type, Context context){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid,context);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (type==WifiConfig.PWD_TYPE.WEP){
            //WEP
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+pws+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }else if(type==WifiConfig.PWD_TYPE.WPA_PSK){
            //WPA
            config.preSharedKey = "\""+pws+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }


    private WifiConfiguration isExist(String ssid,Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\""+ssid+"\"")) {
                return config;
            }
        }
        return null;
    }


    private WifiConfiguration setStaticIpConfiguration(WifiConfiguration config, InetAddress ipAddress, int prefixLength,
                                                 InetAddress gateway, InetAddress[] dns) {
        try{
            // First set up IpAssignment to STATIC.
            Object ipAssignment = getEnumValue(
                    "android.net.IpConfiguration$IpAssignment", "STATIC");
            callMethod(config, "setIpAssignment",
                    new String[] { "android.net.IpConfiguration$IpAssignment" },
                    new Object[] { ipAssignment });

            // Then set properties in StaticIpConfiguration.
            Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");

            Object linkAddress = newInstance("android.net.LinkAddress",
                    new Class[] { InetAddress.class, int.class }, new Object[] {
                            ipAddress, prefixLength });
            setField(staticIpConfig, "ipAddress", linkAddress);
            setField(staticIpConfig, "gateway", gateway);
            ArrayList<Object> aa = (ArrayList<Object>) getField(staticIpConfig, "dnsServers");
            aa.clear();
            for (int i = 0; i < dns.length; i++)
                aa.add(dns[i]);
            callMethod(config, "setStaticIpConfiguration",
                    new String[] { "android.net.StaticIpConfiguration" },
                    new Object[] { staticIpConfig });
            System.out.println("conconconm" + config);
            int updateNetwork = mWifiManager.updateNetwork(config);
            boolean saveConfiguration = mWifiManager.saveConfiguration();
            System.out.println("updateNetwork" + updateNetwork + saveConfiguration);
            System.out.println("ttttttttttt" + "成功");

        }catch (Exception e){
            Log.i("TAG", "setStaticIpConfiguration: 错误了 ");
            e.printStackTrace();
        }
        return config;
    }

    private static Object newInstance(String className)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        return newInstance(className, new Class[0], new Object[0]);
    }



    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object newInstance(String className,
                                      Class[] parameterClasses, Object[] parameterValues)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ClassNotFoundException {
        Class clz = Class.forName(className);
        Constructor constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }



    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object getEnumValue(String enumClassName, String enumValue)
            throws ClassNotFoundException {
        Class enumClz = (Class) Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }



    private static void setField(Object object, String fieldName, Object value)
            throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }



    private static Object getField(Object object, String fieldName)
            throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        Object out = field.get(object);
        return out;
    }



    @SuppressWarnings("rawtypes")
    private static void callMethod(Object object, String methodName,
                                   String[] parameterTypes, Object[] parameterValues)
            throws ClassNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        Class[] parameterClasses = new Class[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterClasses[i] = Class.forName(parameterTypes[i]);

        Method method = object.getClass().getDeclaredMethod(methodName,
                parameterClasses);
        method.invoke(object, parameterValues);
    }
}
