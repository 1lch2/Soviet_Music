package com.example.simplemusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    private static final String TAG = "MusicPlayerService";
    private boolean isPlaying = false;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private MusicPlayerBinder binder = new MusicPlayerBinder(this);

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
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sample));
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared (MediaPlayer mp) {
        Log.d(TAG, "onPrepared: Playing");
//        mp.start();
//        isPlaying = true;
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
        stopSelf();
        Log.d(TAG, "onDestroy: service destroyed.");
    }

    public void playerStart() {
        mMediaPlayer.start();
        isPlaying = true;
        Log.d(TAG, "playerStart: start playing!");
    }

    public void playerPause() {
        mMediaPlayer.pause();
        isPlaying = false;
        Log.d(TAG, "playerPause: music paused!");
    }

    public boolean playerStatus() {
        return isPlaying;
    }

    // TODO: Seek bar
    // TODO: AdapterView
}
