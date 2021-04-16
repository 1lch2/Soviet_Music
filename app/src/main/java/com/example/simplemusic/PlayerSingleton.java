package com.example.simplemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音乐播放器单例<br>
 * 用于维护负责播放音乐的MediaPlayer对象，负责通知的Notification等一类对象。
 * 类对外暴露多个公共方法，通过这些方法可以控制音乐启动停止，切换音乐，同时维护常驻的通知内容。
 * 同时维护一个为进度条计时的Timer，仅当进入PlayerActivity时启动计时任务并移动进度条。
 *
 * @author lichenghao02
 * @since 2021/04/15
 */
public class PlayerSingleton implements MediaPlayer.OnPreparedListener {

    /** 音乐播放状态 */
    private boolean isPlaying = false;
    /** 当前播放音乐的标题 */
    private String currentPlaying = "le_internationale";
    /** 当前播放音乐的序号 */
    private int currentIndex = 2;
    /** 通知ID */
    private static final int notificationId = 616;

    /** 维护的音乐列表对象 */
    private List<Music> mMusicList;
    /** 播放器对象 */
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    /** 读取assets目录的AFD对象 */
    private AssetFileDescriptor mDescriptor;
    /** 常驻通知的Builder对象 */
    private NotificationCompat.Builder mBuilder = null;
    /** 常驻通知的Manager对象 */
    private NotificationManager mManager = null;
    /** 常驻通知对象 */
    private Notification mNotification = null;
    /** 通知使用的大图标对象 */
    private Bitmap largeIcon = null;
    /** 进度条计时器 */
    private Timer mTimer = new Timer();

    /**
     * 持有单例的静态内部类
     */
    private static class SingletonHolder {

        /** 单例的静态实例 */
        private static final PlayerSingleton INSTANCE = new PlayerSingleton();
    }

    /**
     * 类构造方法
     */
    private PlayerSingleton () {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    public static PlayerSingleton getInstance () {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 返回当前播放的音乐标题
     *
     * @return 当前播放音乐的标题
     */
    public String getCurrentPlaying () {
        return currentPlaying;
    }

    /**
     * 设置音乐列表
     *
     * @param musicList 通过assets目录添加的音乐列表
     */
    public void setMusicList (List<Music> musicList) {
        mMusicList = musicList;
    }

    /**
     * 在Service的onCreate生命周期调用的方法。<br>
     * 用于设置默认歌曲（默认为国际歌）并发送常驻通知。
     *
     * @param context 启动方法的上下文
     * @param service 要发送通知变为前台服务的Service对象
     */
    public void onCreate (Context context, Service service) {
        try {
            mDescriptor = context.getAssets().openFd("le_internationale.mp3"); // 默认歌曲为国际歌

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mDescriptor.getFileDescriptor(), mDescriptor.getStartOffset(), mDescriptor
                    .getLength());
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 仅当运行在Android 8以上时发送常驻通知
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            showNotification(context, service);
        }
    }

    @Override
    public void onPrepared (MediaPlayer mp) {
        mp.setLooping(true);
        mp.seekTo(0);
    }

    /**
     * 应用启动时显示前台常驻通知
     * 通知显示当前播放歌曲
     *
     * @param context 启动方法的上下文
     * @param service 要发送通知变为前台服务的Service对象
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification (Context context, Service service) {
        largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cover);

        // Android 8 以上必须有Notification Channel
        NotificationChannel channel = new NotificationChannel("music", "player", NotificationManager.IMPORTANCE_LOW);
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.createNotificationChannel(channel);

        mBuilder = new NotificationCompat.Builder(context, "music");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setContentTitle("Soviet Music player")
                .setContentText("Now playing: " + currentPlaying)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotification = mBuilder.build();
        mManager.notify(notificationId, mNotification);

        // 发送常驻通知将服务变为前台服务
        service.startForeground(notificationId, mNotification);
    }

    /**
     * 点击播放按钮
     */
    public void playerStart () {
        mMediaPlayer.start();
        isPlaying = true;
    }

    /**
     * 点击暂停按钮
     */
    public void playerPause () {
        mMediaPlayer.pause();
        isPlaying = false;
    }

    /**
     * 点击停止按钮
     */
    public void playerStop () {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mDescriptor.getFileDescriptor(), mDescriptor.getStartOffset(), mDescriptor
                    .getLength());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = false;
    }

    /**
     * 切换下一首
     *
     * @param context 调用的Activity的上下文
     */
    public void playerNext (Context context) {
        int currentIndex = playerIndex();
        Music temp;
        if (currentIndex == mMusicList.size() - 1) {
            temp = mMusicList.get(0);
        } else {
            currentIndex++;
            temp = mMusicList.get(currentIndex);
        }
        playerNewStart(temp, context);
    }

    /**
     * 切换上一首
     *
     * @param context 调用的Activity的上下文
     */
    public void playerPrevious (Context context) {
        int currentIndex = playerIndex();
        Music temp;
        if (currentIndex == 0) {
            temp = mMusicList.get(mMusicList.size() - 1);
        } else {
            currentIndex--;
            temp = mMusicList.get(currentIndex);
        }
        playerNewStart(temp, context);
    }

    /**
     * 拖动进度条
     *
     * @param progress 进度条位置
     */
    public void playerSetProgress (int progress) {
        int playerProgress = mMediaPlayer.getDuration();
        mMediaPlayer.seekTo(progress * playerProgress / 100);
    }

    /**
     * 返回当前播放器的播放状态
     *
     * @return 是否在播放音乐
     */
    public boolean playerStatus () {
        return isPlaying;
    }

    /**
     * 返回当前正在播放的歌曲的下标
     *
     * @return 当前音乐的下标
     */
    public int playerIndex () {
        return currentIndex;
    }

    /**
     * 开始播放新歌曲
     *
     * @param music 准备播放的新歌曲对象
     */
    public void playerNewStart (Music music, Context context) {
        try {
            mDescriptor = context.getAssets().openFd(music.getPath());

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mDescriptor.getFileDescriptor(), mDescriptor.getStartOffset(), mDescriptor
                    .getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 设置状态
        isPlaying = true;
        currentPlaying = music.getTitle();
        currentIndex = music.getIndex();

        // 重新发布常驻通知，更新内容
        mBuilder = new NotificationCompat.Builder(context, "music");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setContentTitle("Soviet Music player")
                .setContentText("Now playing: " + currentPlaying)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotification = mBuilder.build();
        mManager.notify(notificationId, mNotification);
    }

    /**
     * 销毁MediaPlayer对象
     */
    public void playerDestroy () {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    /**
     * 启动进度条计时器
     *
     * @param seekBar 进度条的SeekBar对象
     */
    public void timerStart (final SeekBar seekBar) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run () {
                int currentProgress = mMediaPlayer.getCurrentPosition();
                int totalProgress = mMediaPlayer.getDuration();

                seekBar.setProgress(currentProgress * 100 / totalProgress);
            }
        }, 0, 100);
    }

    /**
     * 清空计时器
     */
    public void timerStop () {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
    }

    // TODO: Handler
}
