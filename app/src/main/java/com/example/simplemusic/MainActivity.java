/**
 * @author lichenghao02
 * @since 2021/04/12
 */
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

public class MainActivity extends AppCompatActivity {

    private MusicPlayerService mService;
    private List<Music> mMusicList;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {}
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
        Button stopButton = (Button) findViewById(R.id.button_stop);

        try {
            initMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 设置音乐列表
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.music_list);
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

                // Toast 提示当前播放
                Toast.makeText(MainActivity.this, "Now Playing: "+music.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        // 点击歌曲封面小图(打开播放器界面)
        smallAlbumCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openMainPlayer(v);
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
                try {
                    mService.playerStop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 点击上一首
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // TODO: previous
            }
        });

        // 点击下一首
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // TODO: next
            }
        });
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unbindService(mConnection);

        // TODO: 直接杀死应用时，Service不会销毁
    }

    /**
     * 打开播放器界面
     * @param view 被点击的View，即小封面图
     */
    private void openMainPlayer (View view) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化音乐列表
     * @throws IOException
     */
    private void initMusic() throws IOException {
        AssetManager mAssetManager = getAssets();
        mMusicList = new ArrayList<>();

        for (String filePath : mAssetManager.list("")){
            if (filePath.endsWith(".mp3")) {
                Music tempMusic = new Music();
                tempMusic.setPath(filePath);
                tempMusic.setTitle(filePath.substring(0, filePath.length()-4));

                mMusicList.add(tempMusic);
            }
        }
    }
}