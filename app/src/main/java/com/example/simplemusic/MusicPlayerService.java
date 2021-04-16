package com.example.simplemusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author lichenghao02
 * @since 2021/04/14
 */

/**
 * 音乐播放器服务。<br>
 * 服务持有并维护音乐播放器单例对象，通过与Activity绑定并在通知栏常驻保持运行。
 * 服务在主界面销毁后解除绑定并停止。
 *
 * @see MainActivity
 */
public class MusicPlayerService extends Service {

    private static final String TAG = "MusicPlayerService";

    // 维护的音乐播放器单例以及应用的上下文
    private PlayerSingleton mPlayerSingleton = PlayerSingleton.getInstance();
    private Context mContext = null;
    private MusicPlayerBinder binder = new MusicPlayerBinder(this);

    /**
     * 用于绑定服务的Binder类
     */
    public class MusicPlayerBinder extends Binder {

        private Service currentService;

        public MusicPlayerBinder (Service service) {
            currentService = service;
        }

        MusicPlayerService getService () {
            return (MusicPlayerService) currentService;
        }
    }

    public MusicPlayerService () {
    }

    @Override
    public void onCreate () {
        Log.d(TAG, "onCreate: service created");
        super.onCreate();

        mContext = getApplicationContext();
        mPlayerSingleton.onCreate(mContext, this);
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
     * 点击播放按钮
     */
    public void playerStart () {
        mPlayerSingleton.playerStart();
        Log.d(TAG, "playerStart: start playing!");
    }

    /**
     * 点击暂停按钮
     */
    public void playerPause () {
        mPlayerSingleton.playerPause();
        Log.d(TAG, "playerPause: music paused!");
    }

    /**
     * 点击停止按钮
     */
    public void playerStop () {
        mPlayerSingleton.playerStop();
    }

    /**
     * 返回当前播放器的播放状态
     *
     * @return 是否在播放音乐
     */
    public boolean playerStatus () {
        return mPlayerSingleton.playerStatus();
    }

    /**
     * 返回当前正在播放的歌曲的下标
     *
     * @return 当前音乐的下标
     */
    public int playerIndex () {
        return mPlayerSingleton.playerIndex();
    }

    /**
     * 开始播放新歌曲
     *
     * @param music 准备播放的新歌曲对象
     */
    public void playerNewStart (Music music) {
        mPlayerSingleton.playerNewStart(music, mContext);
    }

    /**
     * 销毁MediaPlayer对象
     */
    public void playerDestroy () {
        mPlayerSingleton.playerDestroy();
    }
}
