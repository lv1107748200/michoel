package com.xxbm.musiclibrary;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;


import com.xxbm.sbecomlibrary.MediaAidlInterface;
import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.utils.NLog;

import java.util.ArrayList;
import java.util.List;


/*
 * lv   2018/9/12
 */
public class MusicPlayerManger {

    public static MediaAidlInterface mService = null;
    private final static List<Context> bindContexts;
    private static   ServiceBinder binder;

    static {
        bindContexts = new ArrayList<>();
    }


    public static void bindStartByContext(Context context, ServiceConnection serviceConnection) {

        Intent i = new Intent().setClass(context, MqService.class);

        if (!bindContexts.contains(context)) {
            // 对称,只有一次remove，防止内存泄漏
            bindContexts.add(context);
        }

        if(null == binder)
        binder = new ServiceBinder(context);

        binder.setmCallback(serviceConnection);

        context.bindService(i, binder, 0);

        if (needMakeServiceForeground(context)) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }

    public void unbindByContext(Context context) {
        if (!bindContexts.contains(context)) {
            return;
        }

        bindContexts.remove(context);

        if (bindContexts.isEmpty()) {

        }

        if(null != binder)
        context.unbindService(binder);
    }

    public static void synthesizeMake(final List<ComAll> comAlls, final int i){
        if(MusicPlayerManger.isNull()){

            bindStartByContext(BaseApplation.getBaseApp(), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    updata(comAlls,i);
                    play();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            });

        }else {
            updata(comAlls,i);
            play();
        }
    }


    public static boolean updata(List<ComAll> comAlls,int i) {
        try {
            if (mService != null) {
                mService.updata(comAlls,i);
                return true;
            }else {
                return false;
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    public static void next() {
        try {
            if (mService != null) {
                mService.next();
            }
        } catch (final Exception ignored) {
        }
    }
    public static void up() {
        try {
            if (mService != null) {
                mService.up();
            }
        } catch (final Exception ignored) {
        }
    }

    public static int play() {
        try {
            if (mService != null) {
                 return    mService.play();
            }
        } catch (final Exception ignored) {
        }

        return -1;
    }
    public static void playHistory() {
        try {
            if (mService != null) {
                mService.playHistory();
            }
        } catch (final Exception ignored) {
        }
    }
    public static void playNum(int num) {
        try {
            if (mService != null) {
                mService.playNum(num);
            }
        } catch (final Exception ignored) {
        }
    }
    public static void seek(long i) {
        try {
            if (mService != null) {
                mService.seek(i);
            }
        } catch (final Exception ignored) {
        }
    }
    public static void pOrq() {
        try {
            if (mService != null) {
                mService.pOrq();
            }
        } catch (final Exception ignored) {
        }
    }
    public static void pause() {
        try {
            if (mService != null) {
                mService.pause();
            }
        } catch (final Exception ignored) {
        }
    }

    public static ComAll getCommAll() {
        try {
            if (mService != null) {
            return     mService.getComAll();
            }
        } catch (final Exception ignored) {
        }
        return null;
    }
    public static List<ComAll> getCommAllList() {
        try {
            if (mService != null) {
                return  mService.getComAllList();
            }
        } catch (final Exception ignored) {
        }
        return null;
    }

    public static int getPlayNumber() {
        try {
            if (mService != null) {
                return  mService.getPlayNumber();
            }
        } catch (final Exception ignored) {
        }
        return 0;
    }

    public static boolean  isPlaying(){
        try {
            if (mService != null) {
               return  mService.isPlaying();
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    public static boolean  isPlayFirst(){
        try {
            if (mService != null) {
                return  mService.isPlayFirst();
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    //MusicPlayer.timing(10 * 60 * 1000);  十分钟结束

    //-1 取消定时 -2 播完当前声音
    public static void timing(int time) {
        if (mService == null) {
            return;
        }
        try {
            mService.timing(time);
        } catch (Exception e) {

        }
    }

    public static void exit() {
        try {
            if (mService != null) {
                mService.exit();
            }
        } catch (final Exception ignored) {
        }
    }
    public static void playPattern(int i) {
        try {
            if (mService != null) {
                mService.playPattern(i);
            }
        } catch (final Exception ignored) {
        }
    }
    //准备完成时  可以返回总时间长度
    public static long duration() {
        try {
            if (mService != null) {
             return    mService.duration();
            }
        } catch (final Exception ignored) {
        }
        return 0;
    }
    public static long position() {
        try {
            if (mService != null) {
                return    mService.position();
            }
        } catch (final Exception ignored) {
        }
        return 0;
    }

    public static void login(int i){
        try {
            if (mService != null) {
                    mService.login(i);
            }
        } catch (final Exception ignored) {
        }
    }

    public static boolean isNull(){
        if(null == mService){
            return true;
        }else {
            return false;
        }
    }

    private static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) return false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (!pm.isInteractive()) return false;
        } else {
            if (!pm.isScreenOn()) return false;
        }

        String packageName = context.getApplicationContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance
                    == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }

        }
        return false;
    }

    public static boolean needMakeServiceForeground(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isAppOnForeground(context);
    }


    public static final class ServiceBinder implements ServiceConnection {
        private  ServiceConnection mCallback;

        public void setmCallback(ServiceConnection mCallback) {
            this.mCallback = mCallback;
        }

        public ServiceBinder(Context context) {

        }

        @Override
        public void onServiceConnected( ComponentName className,  IBinder service) {
            NLog.e(NLog.PLAYER,"绑定回调得到控制实体");
            mService = MediaAidlInterface.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(className, service);
            }
        }

        @Override
        public void onServiceDisconnected( ComponentName className) {
            NLog.e(NLog.PLAYER,"解除绑定");
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            mService = null;
        }
    }

    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;

        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }

}
