package com.kkkkkn.module_wifi.wifi.bean;

public class WifiConfig {
    public String name;
    public String pwd;
    public PWD_TYPE pwdType=PWD_TYPE.WPA_PSK;
    public CONNECT_TYPE connType=CONNECT_TYPE.DHCP;
    public StaticConnConfig staticConnConfig;

    public enum CONNECT_TYPE{
        DHCP,
        STATIC
    }

    public enum PWD_TYPE{
        WEP,
        WPA_PSK,
    }
}
