package com.example.simplemusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 应用的主界面。
 * <p>App启动后进入此界面，通过点击底部小封面图进入PlayerActivity。
 * 应用启动后会启动并绑定MusicPlayerService，并在到达onDestroy声明周期时解绑并停止该Service。
 *
 * @author 1lch2
 * @since 2021/04/12
 */
public class MainActivity extends AppCompatActivity {

    /** 权限请求代码 - 读取外部存储 */
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    /** 权限请求代码 - 前台服务 */
    private static final int PERMISSION_REQUEST_FOREGROUND = 1001;

    /** 音乐播放器单例对象 */
    private final PlayerSingleton mPlayerSingleton = PlayerSingleton.getInstance(this);
    /** 音乐播放器服务 */
    private MusicPlayerService mService;
    /** 从assets目录读取的音乐列表 */
    private List<Music> mMusicList;
    /** 为RecyclerView提供数据的Adapter */
    private MusicAdapter mMusicAdapter;

    /** 用于绑定Service的ServiceConnection对象 */
    private final ServiceConnection mConnection = new ServiceConnection() {
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

        // 在后台读取本地文件并设置音乐列表
        final RecyclerView recyclerView = findViewById(R.id.music_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        new LoadMusicTask(MainActivity.this, recyclerView, playButton).execute();

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
                mPlayerSingleton.playerPrevious();
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
                mPlayerSingleton.playerNext();

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
        this.requestStoragePermission();
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
     * 检查是否已经获得读取存储的权限
     *
     * @return 若已授权则返回true
     */
    private boolean isStoragePermissionGranted() {
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查是否已经获得前台服务的权限
     *
     * @return 若已授权则返回true
     */
    private boolean isForegroundPermissionGranted() {
        return checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 分别请求未获取的权限
     */
    private void requestStoragePermission() {
        if (!isStoragePermissionGranted()) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

        if (!isForegroundPermissionGranted()) {
            requestPermissions(new String[] {Manifest.permission.FOREGROUND_SERVICE}, PERMISSION_REQUEST_FOREGROUND);
        }
    }

    /**
     * 在子线程中加载音乐文件列表
     */
    private static class LoadMusicTask extends AsyncTask<Void, Void, Void> {

        /** MainActivity 的弱引用 */
        private final WeakReference<Context> mContextWeakReference;
        /** RecyclerView 的弱引用 */
        private final WeakReference<RecyclerView> mRecyclerViewWeakReference;
        /** Button 的弱引用 */
        private final WeakReference<Button> mButtonWeakReference;

        /**
         * 构造函数
         *  @param context 后台任务所需的上下文
         * @param recyclerView RecyclerView
         * @param playButton playButton
         */
        public LoadMusicTask (Context context, RecyclerView recyclerView, Button playButton) {
            // 在静态内部类中使用Activity的弱引用来避免内存泄露
            this.mContextWeakReference = new WeakReference<>(context);
            this.mRecyclerViewWeakReference = new WeakReference<>(recyclerView);
            this.mButtonWeakReference = new WeakReference<>(playButton);
        }

        @Override
        protected Void doInBackground (Void... voids) {
            MainActivity mainActivity = (MainActivity)mContextWeakReference.get();

            AssetManager mAssetManager = mainActivity.getAssets();
            mainActivity.mMusicList = new ArrayList<>();

            int index = 0; // 音乐资源的序号
            try {
                for (String filePath : Objects.requireNonNull(mAssetManager.list(""))) {
                    if (filePath.endsWith(".mp3")) {
                        Music tempMusic = new Music();
                        tempMusic.setPath(filePath);
                        tempMusic.setTitle(filePath.substring(0, filePath.length() - 4));
                        tempMusic.setIndex(index++);

                        mainActivity.mMusicList.add(tempMusic);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainActivity.mPlayerSingleton.setMusicList(mainActivity.mMusicList);

            return null;
        }

        @Override
        protected void onPostExecute (Void unused) {
            super.onPostExecute(unused);
            final MainActivity mainActivity = (MainActivity)mContextWeakReference.get();
            final Button playButton = mButtonWeakReference.get();
            RecyclerView recyclerView = mRecyclerViewWeakReference.get();

            mainActivity.mMusicAdapter = new MusicAdapter(mainActivity.mMusicList);
            recyclerView.setAdapter(mainActivity.mMusicAdapter);

            // 为列表项设置点击事件，点击后开始播放对应音乐
            mainActivity.mMusicAdapter.realItemClick(new MusicAdapter.OnItemClickListener() {
                @Override
                public void onItemClick (View view, Music music) {
                    mainActivity.mService.playerNewStart(music);
                    playButton.setBackgroundResource(R.drawable.pause);

                    Toast.makeText(mainActivity,
                                   "Now Playing: " + music.getTitle(),
                                   Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}