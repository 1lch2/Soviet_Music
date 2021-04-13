/**
 * @author lichenghao02
 * @since 2021/04/12
 */
package com.example.simplemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MusicPlayerService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {

        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定服务
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

        // 初始化所有的固定按钮的引用
        ImageView smallAlbumCover = (ImageView) findViewById(R.id.album_cover_small);
        final Button playButton = (Button) findViewById(R.id.button_play);
        Button previousButton = (Button) findViewById(R.id.button_previous);
        Button nextButton = (Button) findViewById(R.id.button_next);

        // 点击歌曲封面小图打开播放器界面
        smallAlbumCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openMainPlayer(v);
            }
        });

        // 点击播放/暂停按钮切换按钮样式
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (mService.playerStatus()) {
                    playButton.setBackgroundResource(R.drawable.play);
                    mService.playerPause();
                } else {
                    playButton.setBackgroundResource(R.drawable.pause);
                    mService.playerStart();
                }
            }
        });

        // TODO: more button click listener
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

        // 解绑服务
        unbindService(mConnection);
    }

    /**
     * 打开播放器界面
     *
     * @param view 被点击的View
     */
    private void openMainPlayer (View view) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        startActivity(intent);
    }
}