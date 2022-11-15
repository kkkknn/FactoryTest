package com.citrontek.module_mic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

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
    private final MediaRecorder mediaRecorder=new MediaRecorder();
    private final MediaPlayer mediaPlayer=new MediaPlayer();
    private AppCompatTextView tv_play_this_time,tv_play_finish_time;
    private File audioFile;
    private final String TAG="MicActivity";

    private final Handler handler=new Handler(Looper.getMainLooper()){
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
            }else if(msg.what==1000){
                tv_play_this_time.setText(ShowTime(msg.arg1));
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

        tv_play_finish_time=findViewById(R.id.play_finish_time);
        tv_play_this_time=findViewById(R.id.play_this_time);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        mediaRecorder.reset();
                        // 第1步：设置音频来源（MIC表示麦克风）
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        //第2步：设置音频输出格式（默认的输出格式）
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        //第3步：设置音频编码方式（默认的编码方式）
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                        //创建一个临时的音频输出文件
                        try {
                            if(audioFile==null){
                                audioFile = File.createTempFile("record_", ".amr");
                            }
                            //第4步：指定音频输出文件
                            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
                            //第5步：调用prepare方法
                            mediaRecorder.prepare();
                            Log.i(TAG, "run: 初始化完成");
                            mediaRecorder.start();
                            handler.sendEmptyMessage(0);
                            Log.i(TAG, "run: 开始录音");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        mediaRecorder.stop();
                        handler.sendEmptyMessage(1);
                        Log.i(TAG, "run: 结束录音");
                    }
                }.start();
            }
        });

        btn_playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Test_recording();
                Log.i(TAG, "run: 开始播放");

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Message msgOk = handler.obtainMessage();
                msgOk.what = 3;
                handler.sendMessage(msgOk);
            }
        });


    }


    /**
     * 播放录音-
     */
    private void Test_recording(){
        handler.sendEmptyMessage(2);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();

            mediaPlayer.start();
            int Alltime= mediaPlayer.getDuration();
            tv_play_finish_time.setText("/"+ShowTime(Alltime));
            new Thread(){
                @Override
                public void run() {
                    while (mediaPlayer.isPlaying()) {
                        try {
                            int CurrentPosition = mediaPlayer.getCurrentPosition();
                            Message message=handler.obtainMessage();
                            message.what=1000;
                            message.arg1=CurrentPosition;
                            handler.sendMessage(message);
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    public String ShowTime(int time){
        time/=1000;
        int minute=time/60;
        int hour=minute/60;
        int second=time%60;
        minute%=60;
        return String.format("%02d:%02d", minute, second);
    }

}