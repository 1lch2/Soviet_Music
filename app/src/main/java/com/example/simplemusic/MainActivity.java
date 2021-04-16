package com.example.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 应用的主界面。
 * <p>App启动后进入此界面，通过点击底部小封面图进入PlayerActivity。
 * 应用启动后会启动并绑定MusicPlayerService，并在到达onDestroy声明周期时解绑并停止该Service。
 *
 * @author lichenghao02
 * @since 2021/04/12
 */
public class MainActivity extends AppCompatActivity {

    /** 音乐播放器单例对象 */
    private PlayerSingleton mPlayerSingleton = PlayerSingleton.getInstance();

    /** 音乐播放器服务 */
    private MusicPlayerService mService;
    /** 从assets目录读取的音乐列表 */
    private List<Music> mMusicList;

    /** 用于绑定Service的ServiceConnection对象 */
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
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

        // 初始化所有的固定按钮的引用
        ImageView smallAlbumCover = findViewById(R.id.album_cover_small);
        final Button playButton = findViewById(R.id.button_play);
        Button previousButton = findViewById(R.id.button_previous);
        Button nextButton = findViewById(R.id.button_next);
        Button stopButton = findViewById(R.id.button_stop);

        try {
            initMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 设置音乐列表
        RecyclerView recyclerView = findViewById(R.id.music_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        MusicAdapter musicAdapter = new MusicAdapter(mMusicList);
        recyclerView.setAdapter(musicAdapter);

        // 为列表项设置点击事件，点击后开始播放对应音乐
        musicAdapter.realItemClick(new MusicAdapter.ItemClickInterface() {
            @Override
            public void onItemClick (View view, Music music) {
                mService.playerNewStart(music);
                playButton.setBackgroundResource(R.drawable.pause);

                Toast.makeText(MainActivity.this, "Now Playing: " + music.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        // 点击歌曲封面小图(打开播放器界面)
        smallAlbumCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openMainPlayer();
            }
        });

        // 点击播放/暂停
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

        // 点击停止播放
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                playButton.setBackgroundResource(R.drawable.play);
                mService.playerStop();
            }
        });

        // 点击上一首
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mPlayerSingleton.playerPrevious(MainActivity.this);
                playButton.setBackgroundResource(R.drawable.pause);
                Toast.makeText(MainActivity.this,
                               "Now Playing: " + mPlayerSingleton.getCurrentPlaying(),
                               Toast.LENGTH_SHORT).show();
            }
        });

        // 点击下一首
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mPlayerSingleton.playerNext(MainActivity.this);

                playButton.setBackgroundResource(R.drawable.pause);
                Toast.makeText(MainActivity.this,
                               "Now Playing: " + mPlayerSingleton.getCurrentPlaying(),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart () {
        super.onStart();
        bindService(new Intent(this, MusicPlayerService.class), mConnection, 0);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unbindService(mConnection);
        stopService(new Intent(this, MusicPlayerService.class));
    }

    /**
     * 进入播放器界面
     */
    private void openMainPlayer () {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化音乐列表
     *
     * @throws IOException assets目录为空时会抛出IOException
     */
    private void initMusic () throws IOException {
        AssetManager mAssetManager = getAssets();
        mMusicList = new ArrayList<>();

        int index = 0; // 音乐资源的序号
        for (String filePath : Objects.requireNonNull(mAssetManager.list(""))) {
            if (filePath.endsWith(".mp3")) {
                Music tempMusic = new Music();
                tempMusic.setPath(filePath);
                tempMusic.setTitle(filePath.substring(0, filePath.length() - 4));
                tempMusic.setIndex(index++);

                mMusicList.add(tempMusic);
            }
        }
        mPlayerSingleton.setMusicList(mMusicList);
    }
}