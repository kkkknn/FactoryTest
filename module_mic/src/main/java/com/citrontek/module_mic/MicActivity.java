package com.citrontek.module_mic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kkkkkn.lib_common.router.CommonRouterConstant;
import com.kkkkkn.module_mic.R;

import java.io.File;
import java.io.IOException;

@Route(path = CommonRouterConstant.MIC)
public class MicActivity extends AppCompatActivity {
    private AppCompatImageButton btn_start,btn_stop,btn_playback;
    private AppCompatButton btn_horn_test1;
    private AppCompatTextView tv_tip_record,tv_tip_play;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private File audioFile;
    private final String TAG="MicActivity";

    private Handler handler=new Handler(Looper.getMainLooper()){
        public void dispatchMessage(android.os.Message msg) {
            if(msg.what==0){
                tv_tip_record.setText("正在录音");
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                btn_playback.setEnabled(false);
            }else if(msg.what==1){
                tv_tip_record.setText("录音结束");
                btn_start.setEnabled(true);
                btn_stop.setEnabled(false);
                btn_playback.setEnabled(true);
            }else if(msg.what==2){
                tv_tip_record.setText("正在播放");
                btn_start.setEnabled(false);
                btn_stop.setEnabled(false);
                btn_playback.setEnabled(false);
            }else if(msg.what==3){
                tv_tip_record.setText("播放完成");
                btn_start.setEnabled(true);
                btn_stop.setEnabled(false);
                btn_playback.setEnabled(true);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic);
        initView();

    }

    //初始化控件
    private void initView() {
        btn_horn_test1=findViewById(R.id.horn_test);
        btn_start=findViewById(R.id.btn_start);
        btn_stop=findViewById(R.id.btn_stop);
        btn_playback=findViewById(R.id.btn_playBack);

        tv_tip_record=findViewById(R.id.tv_tip_record);
        tv_tip_play=findViewById(R.id.tv_tip_play);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Init_recording();
                Log.i(TAG, "run: 初始化完成");
                Start_recording();
                Log.i(TAG, "run: 开始录音");
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stop_recording();
                Log.i(TAG, "run: 结束录音");
            }
        });

        btn_playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Test_recording();
                Log.i(TAG, "run: 开始播放");

            }
        });
    }

    /**
     * 初始录音---设置录音格式，输出目录
     */
    private void Init_recording(){
        mediaRecorder = new MediaRecorder();
        // 第1步：设置音频来源（MIC表示麦克风）
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //第2步：设置音频输出格式（默认的输出格式）
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //第3步：设置音频编码方式（默认的编码方式）
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        //创建一个临时的音频输出文件
        try {
            audioFile = File.createTempFile("record_", ".amr");
            //第4步：指定音频输出文件
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            //第5步：调用prepare方法
            mediaRecorder.prepare();

            // 开启另一个线程获取实时最大音量   mediaRecorder.getMaxAmplitude();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始录音
     */
    private void Start_recording(){
        //调用start方法开始录音
        Message msg = handler.obtainMessage();
        msg.what = 0;
        handler.sendMessage(msg);
        mediaRecorder.start();
    }

    /**
     *  停止录音
     */
    private void Stop_recording(){
        //调用stop方法停止录音
        Message msg = handler.obtainMessage();
        msg.what = 1;
        handler.sendMessage(msg);
        mediaRecorder.stop();
    }

    /**
     * 播放录音-
     */
    private void Test_recording(){
        Message msg = handler.obtainMessage();
        msg.what = 2;
        handler.sendMessage(msg);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Message msgOk = handler.obtainMessage();
                msgOk.what = 3;
                handler.sendMessage(msgOk);
            }
        });

    }
}