package com.kkkkkn.module_wifi.wifi.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.kkkkkn.module_wifi.R;
import com.kkkkkn.module_wifi.wifi.bean.StaticConnConfig;
import com.kkkkkn.module_wifi.wifi.bean.WifiConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogConnWifi extends Dialog implements TextWatcher {
    private AppCompatTextView tv_level,tv_safety;
    private AppCompatCheckBox check_password,check_advanced;
    private AppCompatEditText edit_password;
    private LinearLayoutCompat layoutCompat_advanced,layoutCompat_staticIP;
    private AppCompatRadioButton radio_static,radio_dhcp;
    private AppCompatButton btn_cancel,btn_ok;
    private Listener listener;
    private String wifiName;
    private AppCompatEditText edit_static_ip,edit_static_gateWay,edit_static_dns;

    public DialogConnWifi(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_conn_wifi);

        initView();

        setCancelable(false);
    }

    //设置wifi信息
    public void setWifiInfo(String name,String signal,String safety){
        tv_level.setText(signal);
        tv_safety.setText(safety);
        wifiName=name;
    }

    private void initView() {
        /*//设置弹窗大小
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.6);//设置dialog的宽度为当前手机屏幕的宽度*0.8
        p.height = (int)(size.y * 0.6);//设置dialog的高度为当前手机屏幕的高度*0.8
        getWindow().setAttributes(p);*/

        tv_level=findViewById(R.id.signal_level);
        tv_safety=findViewById(R.id.safety_name);
        edit_password=findViewById(R.id.edit_password);
        check_password=findViewById(R.id.checkbox_password);
        check_advanced=findViewById(R.id.checkbox_advanced);
        layoutCompat_advanced=findViewById(R.id.layout_advanced);
        layoutCompat_staticIP=findViewById(R.id.layout_staticIP);
        radio_dhcp=findViewById(R.id.radio_dhcp);
        radio_static=findViewById(R.id.radio_static);
        btn_cancel=findViewById(R.id.btn_cancel);
        btn_ok=findViewById(R.id.btn_ok);
        edit_static_ip=findViewById(R.id.edit_static_ip);
        edit_static_gateWay=findViewById(R.id.edit_static_gateWay);
        edit_static_dns=findViewById(R.id.edit_static_dns);

        edit_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(radio_static.isChecked()){
                    btn_ok.setEnabled(checkPasswordComplete() && checkStaticIPComplete());
                }else {
                    btn_ok.setEnabled(checkPasswordComplete());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_static_ip.addTextChangedListener(this);
        edit_static_dns.addTextChangedListener(this);
        edit_static_gateWay.addTextChangedListener(this);

        check_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        check_advanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    layoutCompat_advanced.setVisibility(View.VISIBLE);
                }else {
                    layoutCompat_advanced.setVisibility(View.GONE);
                    layoutCompat_staticIP.setVisibility(View.GONE);
                }
            }
        });

        radio_dhcp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    layoutCompat_staticIP.setVisibility(View.GONE);
                    btn_ok.setEnabled(checkPasswordComplete());
                }
            }
        });
        radio_static.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    layoutCompat_staticIP.setVisibility(View.VISIBLE);
                    btn_ok.setEnabled(checkPasswordComplete() && checkStaticIPComplete());
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭弹窗
                dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiConfig wifiConfig=new WifiConfig();
                wifiConfig.name=wifiName;
                Editable editable= edit_password.getText();
                if(editable==null){
                   return;
                }
                //写入密码 加密类型
                wifiConfig.pwdType=getPasswordType();
                wifiConfig.pwd=editable.toString();
                if(radio_static.isChecked()){
                    wifiConfig.connType=WifiConfig.CONNECT_TYPE.STATIC;
                    //写入静态IP配置
                    wifiConfig.staticConnConfig=getStaticConnConfig();
                }
                if(radio_dhcp.isChecked()){
                    wifiConfig.connType=WifiConfig.CONNECT_TYPE.DHCP;
                }
                listener.onClick(wifiConfig);

                dismiss();
            }
        });

    }

    private StaticConnConfig getStaticConnConfig() {
        StaticConnConfig staticConnConfig=new StaticConnConfig();
        Editable editable_ip=edit_static_ip.getText();
        Editable editable_dns=edit_static_dns.getText();
        Editable editable_gateWay=edit_static_gateWay.getText();
        if(editable_ip!=null){
            staticConnConfig.this_ip=editable_ip.toString();
        }
        if(editable_dns!=null){
            staticConnConfig.dns_ip=editable_gateWay.toString();
        }
        if(editable_gateWay!=null){
            staticConnConfig.gateWay_ip=editable_dns.toString();
        }
        return staticConnConfig;
    }

    private WifiConfig.PWD_TYPE getPasswordType() {
        CharSequence charSequence=tv_safety.getText();
        if(charSequence==null||charSequence.length()==0){
            return WifiConfig.PWD_TYPE.WPA_PSK;
        }
        String str=charSequence.toString();
        if(str.contains("WEP")){
            return WifiConfig.PWD_TYPE.WEP;
        }else if(str.contains("WPA_PSK")){
            return WifiConfig.PWD_TYPE.WPA_PSK;
        }else if(str.contains("WPA2_PSK")){
            return WifiConfig.PWD_TYPE.WPA_PSK;
        }
        return WifiConfig.PWD_TYPE.WPA_PSK;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        btn_ok.setEnabled(checkPasswordComplete() && checkStaticIPComplete());
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    public interface Listener{
        void onClick(WifiConfig wifiConfig);
    }

    private boolean checkPasswordComplete(){
        //密码完整性
        Editable editable_password=edit_password.getText();
        return !isEditTextEmpty(editable_password) && editable_password.toString().length() >= 8;
    }

    private boolean checkStaticIPComplete(){
        //判断内容是否为IP
        Editable editable_static_ip=edit_static_ip.getText();
        Editable editable_static_dns=edit_static_dns.getText();
        Editable editable_static_gateWay=edit_static_gateWay.getText();
        if (isEditTextEmpty(editable_static_ip)
                || isEditTextEmpty(editable_static_dns)
                || isEditTextEmpty(editable_static_gateWay)) {
            //是否为空
            return false;
        }else {
            //是否为IP
            return  isIpLegal(editable_static_ip.toString())
                    || isIpLegal(editable_static_dns.toString())
                    || isIpLegal(editable_static_gateWay.toString());
        }
    }



    public static boolean isIpLegal(String ipStr) {
        String ipRegEx = "^([1-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))(\\.([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))){3}$";
        Pattern pattern = Pattern.compile(ipRegEx);
        Matcher matcher = pattern.matcher(ipStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isEditTextEmpty(Editable editable){
        if(editable==null){
            return true;
        }
        return editable.toString().isEmpty();
    }

}
