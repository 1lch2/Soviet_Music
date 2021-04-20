package com.example.simplemusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 音乐播放器界面。<br>
 * 由主界面点击小封面图进入，点击系统返回按钮可以返回到主界面。
 *
 * @author lichenghao02
 * @date 2021/4/12
 */
public class PlayerActivity extends AppCompatActivity {

    /** 播放器单例对象 */
    private PlayerSingleton mPlayerSingleton = PlayerSingleton.getInstance();

    /** 进度条计时器的Handler */
    private Handler mTimerHandler;

    /** 循环调整进度条的Runnable */
    private Runnable mTimerRunnable;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // 初始化所有的固定按钮的引用
        final TextView musicTitle = findViewById(R.id.title_music);
        final Button playButton = findViewById(R.id.button_play_player);
        Button previousButton = findViewById(R.id.button_previous_player);
        Button nextButton = findViewById(R.id.button_next_player);
        final SeekBar progressSeekBar = findViewById(R.id.progressbar);

        // 创建HandlerThread在子线程中运行计时器
        HandlerThread handlerThread = new HandlerThread("timerThread");
        handlerThread.start();

        // 每100毫秒调整一次进度条
        mTimerRunnable = new Runnable() {
            @Override
            public void run () {
                progressSeekBar.setProgress(mPlayerSingleton.getPlayerProgress());
                mTimerHandler.postDelayed(this, 100);
            }
        };

        mTimerHandler = new Handler(handlerThread.getLooper());


        // 按播放状态设置控件外观
        if (mPlayerSingleton.playerStatus()) {
            playButton.setBackgroundResource(R.drawable.pause);
            setTimer();
        } else {
            playButton.setBackgroundResource(R.drawable.play);
        }
        musicTitle.setText(mPlayerSingleton.getCurrentPlaying());

        // 点击播放/暂停
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (mPlayerSingleton.playerStatus()) {
                    playButton.setBackgroundResource(R.drawable.play);
                    mPlayerSingleton.playerPause();
                    stopTimer();
                } else {
                    playButton.setBackgroundResource(R.drawable.pause);
                    mPlayerSingleton.playerStart();
                    setTimer();
                }
            }
        });

        // 点击上一首
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mPlayerSingleton.playerPrevious(PlayerActivity.this);
                playButton.setBackgroundResource(R.drawable.pause);
                Toast.makeText(PlayerActivity.this,
                               "Now Playing: " + mPlayerSingleton.getCurrentPlaying(),
                               Toast.LENGTH_SHORT).show();
                musicTitle.setText(mPlayerSingleton.getCurrentPlaying());
                setTimer();
            }
        });

        // 点击下一首
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mPlayerSingleton.playerNext(PlayerActivity.this);

                playButton.setBackgroundResource(R.drawable.pause);
                Toast.makeText(PlayerActivity.this,
                               "Now Playing: " + mPlayerSingleton.getCurrentPlaying(),
                               Toast.LENGTH_SHORT).show();
                musicTitle.setText(mPlayerSingleton.getCurrentPlaying());
                setTimer();
            }
        });

        // 进度条
        progressSeekBar.setOnSeekBarChangeListener(new MusicProgressBar());
    }

    @Override
    protected void onStop () {
        super.onStop();
        stopTimer();
    }

    /**
     * 启动进度条的计时器
     */
    private void setTimer() {
        mTimerHandler.post(mTimerRunnable);
    }

    /**
     * 销毁进度条的计时器
     */
    private void stopTimer() {
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    /**
     * 进度条监听器对象。<br>
     * 用于维护进度条状态以及拖动进度条的操作
     */
    public class MusicProgressBar implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch (SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch (SeekBar seekBar) {
            // 结束拖动时将音乐进度定位到对应位置
            mPlayerSingleton.playerSetProgress(seekBar.getProgress());
        }
    }
}