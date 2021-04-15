package com.example.simplemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    private static final String TAG = "MusicPlayerService";
    private boolean isPlaying = false;
    private String currentPlaying = "le_internationale";
    private static final int notificationId = 616;

    private AssetFileDescriptor mDescriptor;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private MusicPlayerBinder binder = new MusicPlayerBinder(this);
    private NotificationCompat.Builder mBuilder = null;
    private NotificationManager mManager = null;
    private Notification mNotification = null;
    private Bitmap largeIcon = null;

    public class MusicPlayerBinder extends Binder {
        private Service currentService;

        public MusicPlayerBinder(Service service) {
            currentService = service;
        }

        MusicPlayerService getService() {
            return (MusicPlayerService) currentService;
        }
    }

    public MusicPlayerService () {
    }

    @Override
    public void onCreate () {
        Log.d(TAG, "onCreate: service created");
        super.onCreate();

        // 初始化MediaPlayer
        try {
            mDescriptor = getAssets().openFd("le_internationale.mp3"); // 默认歌曲为国际歌

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mDescriptor.getFileDescriptor(), mDescriptor.getStartOffset(), mDescriptor.getLength());
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            showNotification();
        }
    }

    @Override
    public void onPrepared (MediaPlayer mp) {
        Log.d(TAG, "onPrepared: Playing");
        mp.setLooping(true);
        mp.seekTo(0);
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: service started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind (Intent intent) {
        Log.d(TAG, "onBind: service bound to activity");
        return binder;
    }

    @Override
    public boolean onUnbind (Intent intent) {
        Log.d(TAG, "onUnbind: service unbind");
        mMediaPlayer.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        playerDestroy();
        stopSelf();
        Log.d(TAG, "onDestroy: service destroyed.");
    }

    /**
     * 应用启动时显示前台常驻通知
     * 通知显示当前播放歌曲
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.play);

        // Android 8 以上必须有Notification Channel
        NotificationChannel channel = new NotificationChannel("music", "player", NotificationManager.IMPORTANCE_LOW);
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.createNotificationChannel(channel);

        mBuilder = new NotificationCompat.Builder(this, "music");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setContentTitle("Soviet Music player")
                .setContentText("Now playing: " + currentPlaying)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotification = mBuilder.build();
        mManager.notify(notificationId, mNotification);

        startForeground(notificationId, mNotification);
    }

    // 点击播放按钮
    public void playerStart() {
        mMediaPlayer.start();
        isPlaying = true;
        Log.d(TAG, "playerStart: start playing!");
    }

    // 点击暂停按钮
    public void playerPause() {
        mMediaPlayer.pause();
        isPlaying = false;
        Log.d(TAG, "playerPause: music paused!");
    }

    // 点击停止按钮
    public void playerStop() throws IOException {
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(mDescriptor.getFileDescriptor(), mDescriptor.getStartOffset(), mDescriptor.getLength());
        mMediaPlayer.prepareAsync();
        isPlaying = false;
    }

    // 返回当前播放状态
    public boolean playerStatus() {
        return isPlaying;
    }

    // 点击歌曲开始从头播放
    public void playerNewStart(Music music) {
        try {
            mDescriptor = getAssets().openFd(music.getPath());
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mDescriptor.getFileDescriptor(), mDescriptor.getStartOffset(), mDescriptor.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();

        // 设置相关状态以及通知信息
        isPlaying = true;
        currentPlaying = music.getTitle();

        mBuilder = new NotificationCompat.Builder(this, "music");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setContentTitle("Soviet Music player")
                .setContentText("Now playing: " + currentPlaying)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotification = mBuilder.build();
        mManager.notify(notificationId, mNotification);
    }

    // 销毁MediaPlayer
    public void playerDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    // TODO: Seek bar
    // TODO: PlayerActivity
}
