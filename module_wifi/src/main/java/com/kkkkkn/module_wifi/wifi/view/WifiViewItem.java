package com.kkkkkn.module_wifi.wifi.view;

public class WifiViewItem {
    private String name;
    private int signalLevel;
    private String capabilities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public String getSignalLevelStr(){
        String ret;
        switch (signalLevel){
            case 1:
                ret="弱";
                break;
            case 2:
                ret="一般";
                break;
            case 3:
                ret="强";
                break;
            case 4:
                ret="较强";
                break;
            default:
                ret="无信号";
                break;
        }
        return ret;
    }
}
