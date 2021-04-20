package com.example.simplemusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * 音乐播放器服务。<br>
 * 服务持有并维护音乐播放器单例对象，通过与Activity绑定并在通知栏常驻保持运行。
 * 服务在主界面销毁后解除绑定并停止。
 *
 * @author lichenghao02
 * @since 2021/04/14
 */
public class MusicPlayerService extends Service {

    /** 维护的音乐播放器单例 */
    private PlayerSingleton mPlayerSingleton = PlayerSingleton.getInstance();
    /** 应用的上下文 */
    private Context mContext = null;
    /** 绑定服务的Binder */
    private MusicPlayerBinder binder = new MusicPlayerBinder(this);

    /**
     * 用于绑定服务的Binder类
     */
    public static class MusicPlayerBinder extends Binder {

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
        super.onCreate();

        mContext = getApplicationContext();
        mPlayerSingleton.onCreate(mContext, this);
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind (Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind (Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        playerDestroy();
        stopSelf();
    }

    /**
     * 点击播放按钮
     */
    public void playerStart () {
        mPlayerSingleton.playerStart();
    }

    /**
     * 点击暂停按钮
     */
    public void playerPause () {
        mPlayerSingleton.playerPause();
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
