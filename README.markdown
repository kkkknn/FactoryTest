# FactoryTest
Android出厂测试软件<br/>
**注：指纹与手写笔测试用的公司内部硬件和接口，其它设备无法使用**<br/>
Android开发经验不足，有些逻辑比较啰嗦，有问题希望大家指出来 :)
## 项目日志
2019年5月13日：创建项目

2019年5月20日：版本信息显示和返回
2019年5月21日：背光测试

## 项目内容
1. [主界面布局编写](#main_activity)
1. [版本信息显示](#version_information)
1. [屏幕颜色测试](#lcd)
1. [背光灯测试](#backlight)
1. 按键拦截测试
1. 相机测试
1. 充电器测试
1. WiFi测试
1. 录音测试
1. 电容屏测试
1. 喇叭测试
1. 手写笔测试
1. 指纹测试

### main_activity
思路：采用Xml文件配置的形式进行测试项目的存储，和添加到Listview当中，使用自定义adapter来实现<br/>

Item类用来实现listview中子控件<br/>

ItemAdapter类继承ArrayAdapter类重写getview方法和刷新方法<br/>

界面切换通过startActivityForResult来实现，startActivityForResult来传递listview中的焦点位<br/>

最终通过Mainactivity中的onActivityResult来获取和刷新listview数据


### version_information
获取Android版本信息

通过Build基类来获取各个版本号:
~~~
软件版本信息
Build.VERSION.CODENAME        固件版本
Build.VERSION.INCREMENTAL     基带版本
Build.VERSION.RELEASE         发布版本
Build.VERSION.SDK_INT         Android版本
Build.getRadioVersion         无线电固件版本
Build.DISPLAY                 版本号

硬件版本信息
Build.BOARD                   主板
Build.MODEL                   用户可见名称
Build.DEVICE                  设备参数
Build.MANUFACTURER            硬件制造商
~~~

### backlight
要点：通过以下方法来设置屏幕背光,大小为浮点值0.0f~1.0f。
    
**此方法修改的背光亮度只在当前界面有效，一旦切换便会失效**

~~~
Window window = Backlight.this.getWindow();
WindowManager.LayoutParams layoutParams = window.getAttributes();
layoutParams.screenBrightness = back_light;
window.setAttributes(layoutParams);
~~~

### lcd
