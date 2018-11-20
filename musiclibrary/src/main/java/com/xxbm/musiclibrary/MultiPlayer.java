package com.xxbm.musiclibrary;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.text.TextUtils;

import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.xxbm.sbecomlibrary.utils.SdcardUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.utils.NLog;

/*
 * lv   2018/9/14
 */
public class MultiPlayer implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnPreparedListener  {

    private final WeakReference<MqService> mService;

    public MultiPlayer(WeakReference<MqService> mService) {
        this.mService = mService;
    }
    private MediaPlayer mCurrentMediaPlayer;

    public void setDataSource(final String path) {
        if(null == mCurrentMediaPlayer){
           // MLog.E("播放器  setDataSource null");

            mCurrentMediaPlayer =   new MediaPlayer();
            mCurrentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // mCurrentMediaPlayer.setAudioSessionId(getAudioSessionId());
            mCurrentMediaPlayer.setOnPreparedListener(this);
            mCurrentMediaPlayer.setOnCompletionListener(this);
            mCurrentMediaPlayer.setOnErrorListener(this);
            mCurrentMediaPlayer.setOnSeekCompleteListener(this);
            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);

            setPath(path);
            mCurrentMediaPlayer.prepareAsync();
        }else {
           // MLog.E("播放器  setDataSource 不为空");
            mCurrentMediaPlayer.reset();

            setPath(path);

            mCurrentMediaPlayer.prepareAsync();

        }
    }

    private void setPath(String s){
        try {
                mCurrentMediaPlayer.setDataSource(s);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying(){
        if(null == mCurrentMediaPlayer)
            return false;

        boolean is = mCurrentMediaPlayer.isPlaying();
        //NLog.e(NLog.PLAYER,"播放器正在播放isPlaying " + is);

        return is;
    }

    public void start() {
        NLog.e(NLog.PLAYER,"播放器  start");
        if(null == mCurrentMediaPlayer)
            return;
        mCurrentMediaPlayer.start();
    }
    public void pause() {
        NLog.e(NLog.PLAYER,"播放器  pause");
        if(null == mCurrentMediaPlayer)
            return;
        mCurrentMediaPlayer.pause();
    }
    public void stop() {
        NLog.e(NLog.PLAYER,"播放器  stop");
        if(null == mCurrentMediaPlayer)
            return;
        mCurrentMediaPlayer.stop();
    }
    public void release() {
        NLog.e(NLog.PLAYER,"播放器  release");
        if(null == mCurrentMediaPlayer)
            return;
        stop();
        mCurrentMediaPlayer.release();
    }
    public long duration() {
         //准备完成时  可以返回总时间长度
            return mCurrentMediaPlayer.getDuration();
    }
    public long position() {//准备完成时  可以返回已播放时间
        return mCurrentMediaPlayer.getCurrentPosition();
    }

    public long seek(final long whereto) {
        mCurrentMediaPlayer.seekTo((int) whereto);
        return whereto;
    }
    public void setVolume(final float vol) {
        try {
            mCurrentMediaPlayer.setVolume(vol, vol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAudioSessionId() {
        return mCurrentMediaPlayer.getAudioSessionId();
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mService.get().onPrepared(mp);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        mService.get().onCompletion(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mService.get().onError(mp,what,extra);
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mService.get().onSeekComplete(mp);
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }


    public boolean createPath( String url) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String
       // pathttt = SdcardUtil.getDiskFileDir(BaseApplation.getBaseApp(),MqService.pathSong);
        pathttt = SdcardUtil.getDiskFileDir(BaseApplation.getBaseApp()) + File.separator + MqService.pathSong;;
        pathttt = FileDownloadUtils.generateFilePath(pathttt,SdcardUtil.md5(url));

        boolean is = fileIsExists(pathttt);

       // MLog.E("执行 createPath" + pathttt + "  存在否  " +is);
        return is;
    }
}
