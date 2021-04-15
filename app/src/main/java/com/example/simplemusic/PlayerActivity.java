/**
 * @author lichenghao02
 * @since 2021/04/12
 */
package com.example.simplemusic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";

    private PlayerSingleton mPlayerSingleton = PlayerSingleton.getInstance();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Log.d(TAG, "onCreate: playerActivity created");

        // 初始化所有的固定按钮的引用
        final TextView musicTitle = (TextView) findViewById(R.id.title_music);
        final Button playButton = (Button) findViewById(R.id.button_play_player);
        Button previousButton = (Button) findViewById(R.id.button_previous_player);
        Button nextButton = (Button) findViewById(R.id.button_next_player);
        final SeekBar progressSeekBar = (SeekBar) findViewById(R.id.progressbar);

        // 按播放状态设置控件外观
        if (mPlayerSingleton.playerStatus()) {
            playButton.setBackgroundResource(R.drawable.pause);
            mPlayerSingleton.timerStart(progressSeekBar);
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
                    mPlayerSingleton.timerStop();
                } else {
                    playButton.setBackgroundResource(R.drawable.pause);
                    mPlayerSingleton.playerStart();
                    mPlayerSingleton.timerStart(progressSeekBar);
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
                mPlayerSingleton.timerStart(progressSeekBar);
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
                mPlayerSingleton.timerStart(progressSeekBar);
            }
        });

        // 进度条
        progressSeekBar.setOnSeekBarChangeListener(new MusicProgressBar());

    }

    @Override
    protected void onStart () {
        super.onStart();
        Log.d(TAG, "onStart: started!");
    }

    @Override
    protected void onStop () {
        super.onStop();
        Log.d(TAG, "onStop: stopped!");
        mPlayerSingleton.timerStop();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        Log.d(TAG, "onDestroy: destroyed");
    }

    public class MusicProgressBar implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch (SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch (SeekBar seekBar) {
            mPlayerSingleton.playerSetProgress(seekBar.getProgress());
        }
    }
}