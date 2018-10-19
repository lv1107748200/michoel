package fm.qian.michael.service;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.SdcardUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpUtils;
import fm.qian.michael.receiver.MediaButtonIntentReceiver;
import fm.qian.michael.ui.activity.LockActivity;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.widget.single.DownManger;
import fm.qian.michael.widget.single.UserInfoManger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static fm.qian.michael.common.GlobalVariable.BaseUrlONE;
import static fm.qian.michael.common.UserInforConfig.USERPLAYId;
import static fm.qian.michael.common.UserInforConfig.USERPLAYSEEK;
import static fm.qian.michael.common.UserInforConfig.USERTMEING;

/*
 * lv   2018/9/12
 */
public class MqService extends Service {

    public static final String pathSong = "song";

    public static final String UPDATALOGIN = "fm.qian.michael.loginchanged";
    public static final String UPDATA_ID = "fm.qian.michael.playstatechanged";
    public static final String UPDATA_PAUSE = "fm.qian.michael.updatepause";
    public static final String SERVICECMD = "fm.qian.michael.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "fm.qian.michael.togglepause";
    public static final String PAUSE_ACTION = "fm.qian.michael.pause";
    public static final String PAUSE_ACTION_APP = "fm.qian.michael.pauseApp";
    public static final String STOP_ACTION = "fm.qian.michael.stop";
    public static final String PREVIOUS_ACTION = "fm.qian.michael.previous";
    public static final String PREVIOUS_FORCE_ACTION = "fm.qian.michael.previous.force";
    public static final String NEXT_ACTION = "fm.qian.michael.next";
    public static final String NEXT_UP = "fm.qian.michael.up";
    public static final String REPEAT_ACTION = "fm.qian.michael.repeat";
    public static final String SHUFFLE_ACTION = "fm.qian.michael.shuffle";
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String UPDATE_LOCKSCREEN = "fm.qian.michael.updatelockscreen";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDUP = "up";
    public static final String CMDNOTIF = "buttonId";
    public static final String LOCK_SCREEN = "fm.qian.michael.lock";
    public static final String SEND_PROGRESS = "fm.qian.michael.progress";
    public static final String MUSIC_LODING = "fm.qian.michael.loading";
    public static final String BUFFER_UP = "fm.qian.michael.bufferup";
    private static final String SHUTDOWN = "fm.qian.michael.shutdown";
    public static final String SETQUEUE = "fm.qian.michael.setqueue";

    public static final int REQDATA = 6;//请求播放地址
    public static final int REQICON = 5;//请求icon
    public static final int REQNFTION = 4;//
    public static final int REQGETSAVE = 3;// 获取保存的数据

    private ComAll comAll; //代表当前正在播放的  id;

    private static final int CIRCULATION = 0;//循环
    private static final int RANDOM = 1;//随机
    private static final int SINGLE = 2;//单曲
    private  int whatPattern = CIRCULATION;

    private List<ComAll> comAllList;
    private IBinder mBinder;

    private MultiPlayer multiPlayer;
    private int playNumber;
    private AudioManager mAudioManager;
    private ComponentName mMediaButtonReceiverComponent;
    private boolean mIsSending = false;//发送进度
    private boolean isSendking = false;//正在快进
    private boolean isFirstLoad = true;//跟新数据
    private boolean mIsTrackPrepared = false;//是否准备完成
    private boolean isCom =false;//播放完成

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private int mNotificationId = 1000;

    private long press = -1; //播放进度

    private boolean mIsLocked;

    private HandlerThread mHandlerThread;
    private MusicPlayerHandler mPlayerHandler;
    private MainPlayerHandler mainPlayerHandler;

//    private AlarmManager mAlarmManager;
//    private PendingIntent mShutdownIntent;
//    private boolean mShutdownScheduled = false;
    private AlarmManager mAlarmManager;
    private PendingIntent pendingIntent;

    private int mServiceStartId = -1;

    private boolean isNext = true;//下一曲
    private boolean isNextPalyer = false;//播放器自己进行下一曲

    private int PlayOnclickNum = -1;//直接播放第几个

    private boolean isPlayNow = false;//完成当前播放任务后停止

    private boolean isLogin;//是否登陆


    //private boolean isTermination = false;//强行终止异步操作
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        NLog.e(NLog.PLAYER,"返回 mBinder");
       // cancelShutdown();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        NLog.e(NLog.PLAYER,"执行 onRebind");
       // cancelShutdown();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        NLog.e(NLog.PLAYER,"执行 onUnbind");
        stopSelf(mServiceStartId);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NLog.e(NLog.PLAYER,"执行 onCreate");

        FlowManager.init(new FlowConfig.Builder(this).build());

        comAllList  = new ArrayList<>();

        mBinder  = new MServiceHandler(new WeakReference<>(this));

        multiPlayer = new MultiPlayer(new WeakReference<>(this));//初始化音乐播放器

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//

        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mPlayerHandler = new MusicPlayerHandler(this, mHandlerThread.getLooper());
        mainPlayerHandler = new MainPlayerHandler(this,this.getMainLooper());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaButtonReceiverComponent = new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);


        // Initialize the intent filter and each action
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICECMD);
        filter.addAction(TOGGLEPAUSE_ACTION);
        filter.addAction(PAUSE_ACTION);
        filter.addAction(STOP_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(PREVIOUS_ACTION);
        filter.addAction(PREVIOUS_FORCE_ACTION);
        filter.addAction(REPEAT_ACTION);
        filter.addAction(SHUFFLE_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_OFF);//关屏
        filter.addAction(Intent.ACTION_SCREEN_ON);//亮屏
        filter.addAction(Intent.ACTION_USER_PRESENT);//解锁
        filter.addAction(LOCK_SCREEN);
        filter.addAction(SETQUEUE);
//        filter.addAction(UPDATALOGIN);
        // Attach the broadcast listener
        registerReceiver(mIntentReceiver, filter);

        UseData useData = UseData.getUseData();
        if(!CheckUtil.isEmpty(useData.getSessionkey())){
            isLogin = true;
        }else {
            isLogin = false;
        }
        whatPattern = useData.getPattern();
        NLog.e(NLog.PLAYER,"播放模式 --->" + whatPattern);
//        Intent shutdownIntent = new Intent(this, MqService.class);
//        shutdownIntent.setAction(SHUTDOWN);
//        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NLog.e(NLog.PLAYER,"执行 onStartCommand" + " mServiceStartId: "+ startId
        );
        mServiceStartId = startId;


        if (intent != null) {
                handleCommandIntent(intent);//onStartCommand
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NLog.e(NLog.PLAYER,"执行 onDestroy");
        cancelNotification();
        multiPlayer.release();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        unregisterReceiver(mIntentReceiver);
        mHandlerThread.quit();
        Intent audioEffectsIntent = new Intent(
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(audioEffectsIntent);
        super.onDestroy();

//        mAlarmManager.cancel(mShutdownIntent);
    }


    //初始化 更新 播放列表  每次更新 都按第一次进来处理 isFirstLoad 设置为 true
    public void updata(List<ComAll> comAll,int num){
        synchronized (this) {
            isFirstLoad = true;
            playNumber = num;
            if(!CheckUtil.isEmpty(comAll)){
                this.comAllList.clear();
                this.comAllList.addAll(comAll);

                mPlayerHandler.post(saveData);
            }
        }
    }

    //传进播放地址
    public void play() {

        if(!isCanPlay()){
            NToast.shortToastBaseApp("需登陆");
            return;
        }

            if(isFirstLoad){//处理点击已经正在播放的音频
                if(null != comAll && !CheckUtil.isEmpty(comAllList)){

                    NLog.e(NLog.PLAYER,"开始 play " + " isFirstLoad :" + isFirstLoad
                            + "  comAll.getId() : " + comAll.getId()
                            + " comAllList.get(playNumber).getId() : " + comAllList.get(playNumber).getId());

                    if(comAll.getId().equals(comAllList.get(playNumber).getId())){
                        NLog.e(NLog.PLAYER, "播放器处于当前歌曲位置");
                        isFirstLoad = false;
                        return;
                    }
                }
            }



        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        NLog.e(NLog.PLAYER, "传入数据  play  playback: audio focus request status = " + status);
        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
       //intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);

       // pushAction(MUSIC_LODING);//正在加载
        if(isPlaying()){
            pause();//换歌时暂停
        }
        getPlayPath();

        mainPlayerHandler.sendEmptyMessage(REQNFTION);

    }
    //确认播放调用
    private void playQQQ(String pathSong){
        if(CheckUtil.isEmpty(pathSong)){
            setDataSource(comAll.getUrl());//获取链表中数据进行播放
        }else {
            setDataSource(pathSong);//直接播放传入地址
        }
    }

    //直接进行历史播放
    public void playHistory(){
        synchronized (this) {
            String id = UseData.getUseData().getHistoryId();

            if(!CheckUtil.isEmpty(id)){

                for(int i = 0,j=comAllList.size(); i<j; i++){
                    ComAll comAll = comAllList.get(i);
                    if(comAll.getId().equals(id)){
                        playNumber = i;
                        break;
                    }
                }

                press = UseData.getUseData().getProgress();


            }else {
                playNumber = 0;
            }

            play();//历史播放
        }


    }

    public void playNum(int i){
        synchronized (this) {
            PlayOnclickNum = i;
            play();//选集播放
        }
    }

    public void setDataSource(final String path) {
        if(CheckUtil.isEmpty(path))
            return;
        mIsTrackPrepared = false;
        multiPlayer.setDataSource(path);
        isCom = false;
    }

    public void start(){
        if(!isCanPlay()){
            NToast.shortToastBaseApp("付费专辑需登陆播放");
            return;
        }

        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        NLog.e(NLog.PLAYER, "Starting playback: audio focus request status = " + status);
        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }
//        Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
//        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
//        sendBroadcast(intent);

        synchronized (this) {
            if(mIsTrackPrepared){
                multiPlayer.start();

               // updateNotification("开始播放");//开始播放


                mainPlayerHandler.sendEmptyMessage(REQNFTION);

                mIsSending = false;
                pushAction(SEND_PROGRESS);//发送一次进度杀心广播
            }else {

            }
        }

    }

    public void pOrq(){
        boolean is = isPlaying();
        NLog.e(NLog.PLAYER,"播放器正在播放isPlaying " + is);
        if(is){
            pause();//app 按钮 触发
        }else {
            start();//app 按钮 触发
        }

    }

    public void pause(){
        synchronized (this) {

            final Intent intent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
            // intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
            sendBroadcast(intent);

            multiPlayer.pause();
            pushAction(UPDATA_PAUSE);//暂停时调用
            //updateNotification("暂停播放");//暂停播放

            mainPlayerHandler.sendEmptyMessage(REQNFTION);

            if(null != comAll){
                UseData.setWhat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        -1,
                        -1,
                        comAll.getId(),
                        position()
                );
            }
        }
    }

    public void pauseHeHe(){

            final Intent intent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
            // intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
            sendBroadcast(intent);

            multiPlayer.pause();
            pushAction(UPDATA_PAUSE);//暂停时调用

            if(null != comAll){
                UseData.setWhat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        -1,
                        -1,
                        comAll.getId(),
                        position()
                );
            }

    }

    public void stop() {
        multiPlayer.stop();
    }


    public void next(){
        isNext = true;
        play();//下一集
    }
    public void up(){
        isNext = false;
        play();//上一集
    }
    public long duration(){
        if(mIsTrackPrepared)
        return multiPlayer.duration();
        return 0;
    }
    public long position(){
        if(mIsTrackPrepared)
        return multiPlayer.position();
        return 0;
    }

    public long seek(long pos){
        isSendking = true;
        multiPlayer.seek(pos);
        return pos;
    }

    public ComAll getComAll(){
        return comAll;
    }
    public List<ComAll> getComAllList(){
        return comAllList;
    }
    public int getPlayNumber() {
        return playNumber;
    }
    public void login(int index){

        if(index == 0){//登陆
            isLogin = true;
        }else if(index == 1){//退出
            isLogin = false;
            if(isPay()){
                pause();
            }

        }

    }

    private boolean isPay(){
        if(null != comAll){
            if(GlobalVariable.ONE.equals(comAll.getIspay())){
                return true;
            }
        }
        return false;
    }
    private boolean isCanPlay(){
        if(isPay()){
            if(isLogin){
               return true;
            }else {
                return false;
            }
        }else {
            return true;
        }
    }
    /**
     * AlarmManager.ELAPSED_REALTIME：使用相对时间，可以通过SystemClock.elapsedRealtime() 获取（从开机到现在的毫秒数，包括手机的睡眠时间），设备休眠时并不会唤醒设备。
     AlarmManager.ELAPSED_REALTIME_WAKEUP：与ELAPSED_REALTIME基本功能一样，只是会在设备休眠时唤醒设备。
     AlarmManager.RTC：使用绝对时间，可以通过 System.currentTimeMillis()获取，设备休眠时并不会唤醒设备。
     AlarmManager.RTC_WAKEUP: 与RTC基本功能一样，只是会在设备休眠时唤醒设备。
     * @param time
     */
    public void timing(int time){
        if(time == -1){
            if(null != mAlarmManager)
            mAlarmManager.cancel(pendingIntent);
        }else if( time == -2){//播完当前
            isPlayNow = true;
        } else {
            if(null == mAlarmManager){
                pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(PAUSE_ACTION),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);
            }else {
                mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);
            }
        }
    }
    public void exit() {
        releaseServiceUiAndStop();//退出
    }
    //设置播放模式 循环 单曲  随机
    public void playPattern(int pattern)  {
        whatPattern = pattern;
    }
    public void onPrepared(MediaPlayer mp){
        NLog.e(NLog.PLAYER,"播放器  onPrepared");
        mIsTrackPrepared = true;

        if(isFirstLoad){
            isFirstLoad = false;
        }
        start();//准备完成后调用

        mIsSending = false;
        if(press > -1){
            seek(press);//有快进记录
            press = -1;

        }else {
          //  pushAction(SEND_PROGRESS);//播放器  onPrepared
        }
    }
    public void onCompletion(MediaPlayer mp) {
        NLog.e(NLog.PLAYER,"播放器  onCompletion");
        isCom = true;
        if(isPlayNow){
            isPlayNow = false;
          //  SPUtils.putInt(USERTMEING,0);//定时器完成 播哦放完当前

            UseData.setSTimeing(0);

            pushAction(PAUSE_ACTION_APP);//发送定时器暂停广播
            pause();//定时任务 播放完当前
        }else {
            isNextPalyer = true;
            next();//播放器 进行的下一曲
        }
    }
    public void onSeekComplete(MediaPlayer mp) {
        NLog.e(NLog.PLAYER,"播放器  onSeekComplete");
        isSendking = false;
        if(isCanPlay()){//快进完成
            pushAction(SEND_PROGRESS);//快进完成
        }

    }
    public boolean onError(MediaPlayer mp, int what, int extra) {
        NLog.e(NLog.PLAYER,"播放器  onError");
        return false;
    }

    public int getAudioSessionId() {
        synchronized (this) {
            return multiPlayer.getAudioSessionId();
        }
    }
    public boolean isPlaying() {
        return multiPlayer.isPlaying();
    }
    public boolean isPlayFirst() {
        return  isFirstLoad;
    }
    private String getPlayPath(){
        NLog.e(NLog.PLAYER,"获取播放地址 开始时 playNumber" + playNumber +" PlayOnclickNum : "+PlayOnclickNum
        + " isFirstLoad :" + isFirstLoad + "  whatPattern : " + whatPattern + " isNextPalyer : " + isNextPalyer);

        String path = null;
        if(comAllList.size() == 0){
            isFirstLoad = true;//在列表为空时 获取历史时 设为重新加载

            getSave();//
          //  mainPlayerHandler.sendEmptyMessage(REQGETSAVE);

            return path;
        }else {

        }
        if(PlayOnclickNum>=0){

            playNumber = PlayOnclickNum;
            PlayOnclickNum = -1;

        }else if(!isFirstLoad){
            switch (whatPattern){
                case CIRCULATION://循环
                    if(isNext){
                        playNumber++;
                    }else {
                        playNumber--;
                    }
                    break;
                case RANDOM://随机
                    Random random = new Random();
                    playNumber =  random.nextInt(comAllList.size());
                    break;
                case SINGLE://单曲

                    if(isNextPalyer){
                        isNextPalyer = false;
                    }else {
                        if(isNext){
                              playNumber++;
                        }else {
                              playNumber--;
                        }
                    }

                    break;
            }
        }


        if(playNumber >= comAllList.size()){
            playNumber = 0;
        }else if(playNumber< 0){
            playNumber = comAllList.size() -1;
        }
        NLog.e(NLog.PLAYER,"获取播放地址 结束 playNumber ：" + playNumber );

        comAll = comAllList.get(playNumber);//当前获取的 播放

        path = comAll.getUrl();//播放地址

        reqData(comAll.getId());//播放时检测到播放地址为空

        return path;
    }


//    private void  scheduleDelayedShutdown() {
//         L.E("Scheduling shutdown in " + IDLE_DELAY + " ms");
//        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent);
//        mShutdownScheduled = true;
//    }
//
//    private void cancelShutdown() {
//        L.E( "Cancelling delayed shutdown, scheduled = " + mShutdownScheduled);
//        if (mShutdownScheduled) {
//            mAlarmManager.cancel(mShutdownIntent);
//            mShutdownScheduled = false;
//        }
//    }

    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = SERVICECMD.equals(action) ? intent.getStringExtra(CMDNAME) : null;

        NLog.e(NLog.PLAYER,"广播: action = " + action + ", command = " + command);

        if (CMDNEXT.equals(command) || NEXT_ACTION.equals(action)) {
            next();
        }else if(CMDUP.equals(command) || NEXT_UP.equals(action)){
            up();
        }else if (CMDTOGGLEPAUSE.equals(command) || TOGGLEPAUSE_ACTION.equals(action)) {
            boolean is = isPlaying();
            NLog.e(NLog.PLAYER,"播放器正在播放isPlaying " + is);
            if (is) {
                pause();//
            } else {
                if(isFirstLoad){
                    updata(null,0);
                    play();
                }else {
                    start();//外部按钮
                }

            }
        } else if (CMDPAUSE.equals(command)) {
            pause();
        }else if( PAUSE_ACTION.equals(action)) {
            UseData.setSTimeing(0);
            pushAction(PAUSE_ACTION_APP);//发送定时器暂停广播
            pause();
        } else if (CMDPLAY.equals(command)) {
            start();//多媒体控制
        } else if (CMDSTOP.equals(command) || STOP_ACTION.equals(action)) {
            closeNotification();//广播结束
        }else if (Intent.ACTION_SCREEN_OFF.equals(action) ){

            if(!mIsLocked && !isFirstLoad){
                mIsLocked = true;
                Intent lockscreen = new Intent(this, LockActivity.class);
                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockscreen);
            }

        }else if(Intent.ACTION_USER_PRESENT.equals(action)){


        } else if (LOCK_SCREEN.equals(action)){

            mIsLocked = intent.getBooleanExtra("islock",true);

        }else if(UPDATALOGIN.equals(action)){

        }
    }
    public void updateNotification(final String what) {

        if(null == comAll)
            return;

        if(!CheckUtil.isEmpty(comAll.getCover_small())){
            RequestOptions options = new RequestOptions()
                    .error(R.mipmap.logo)
                    .priority(Priority.LOW);
            Glide.with(this)
                    .asBitmap()
                    .apply(options)
                    .load(comAll.getCover_small())
                    .into(new SimpleTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            startForeground(mNotificationId, getNotification(what,resource));

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                            startForeground(mNotificationId, getNotification(what,null));

                        }
                    });
        }else {
            startForeground(mNotificationId, getNotification(what,null));
        }

    }

    private Notification getNotification(String what,Bitmap bitmap) {
        RemoteViews remoteViews;
        final int PAUSE_FLAG = 0x1;
        final int NEXT_FLAG = 0x2;
        final int UP_FLAG = 0x4;
        final int STOP_FLAG = 0x3;
        final String albumName = comAll.getTitle();
        final String artistName = comAll.getBroad();

        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);

        String text = artistName;

        remoteViews.setTextViewText(R.id.title, albumName);
        remoteViews.setTextViewText(R.id.text, text);

        //此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent pauseIntent = new Intent(TOGGLEPAUSE_ACTION);
        pauseIntent.putExtra("FLAG", PAUSE_FLAG);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);

        boolean is = isPlaying();
        NLog.e(NLog.PLAYER,"播放器正在播放isPlaying " + is);

        remoteViews.setImageViewResource(R.id.iv_pause, is ? R.drawable.note_btn_pause : R.drawable.note_btn_play);
        remoteViews.setOnClickPendingIntent(R.id.iv_pause, pausePIntent);

        Intent nextIntent = new Intent(NEXT_ACTION);
        nextIntent.putExtra("FLAG", NEXT_FLAG);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPIntent);

        Intent preIntent = new Intent(STOP_ACTION);
        preIntent.putExtra("FLAG", STOP_FLAG);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_stop, prePIntent);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0,
//                new Intent(this.getApplicationContext(), PlayingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        final Intent nowPlayingIntent = new Intent();
        //nowPlayingIntent.setAction("com.wm.remusic.LAUNCH_NOW_PLAYING_ACTION");
        nowPlayingIntent.setComponent(new ComponentName("fm.qian.michael","fm.qian.michael.ui.activity.dim.PlayActivity"));
        nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent click = PendingIntent.getActivity(this,0,nowPlayingIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        if(null != bitmap){
            remoteViews.setImageViewBitmap(R.id.image, bitmap);
        }else {
         //   remoteViews.setImageViewResource(R.id.image, R.drawable.logo);
            remoteViews.setImageViewResource(R.id.image, R.drawable.placeholder_disk_210);
        }

//        if("0".equals(what)){
//          //  NLog.e(NLog.PLAYER,"获取 图片 ");
//          //  mPlayerHandler.post(reqIcon);//执行图片请求
//        }

        if(mNotification == null){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContent(remoteViews)
                    .setSmallIcon(R.drawable.playbg)
                    .setContentIntent(click);
//                    .setWhen(mNotificationPostTime);
            if (CommonUtils.isJellyBeanMR1()) {
                builder.setShowWhen(false);
            }
            mNotification = builder.build();
        }else {
            mNotification.contentView = remoteViews;
        }

        return mNotification;
    }

    private void cancelNotification() {
        stopForeground(true);
        //mNotificationManager.cancel(hashCode());
        mNotificationManager.cancel(mNotificationId);
    }

    public void pushAction(String ACTION) {
        NLog.e(NLog.PLAYER,"widget"+"action = " + ACTION);
        Intent startIntent = new Intent(ACTION);
        sendBroadcast(startIntent);
    }

    public void pushAction(String ACTION,String what) {
        NLog.e(NLog.PLAYER,"widget"+"action = " + ACTION);
        Intent startIntent = new Intent(ACTION);
        startIntent.putExtra("PLAYID",what);
        sendBroadcast(startIntent);
    }


    private void closeNotification(){
        pauseHeHe();//关闭 Notification
        cancelNotification();
    }

    private void releaseServiceUiAndStop() {
        NLog.e(NLog.PLAYER,"执行 releaseServiceUiAndStop");
        pauseHeHe();//释放是暂停
        stopSelf(mServiceStartId);
    }


    //获取去 本地保存的记录
    public void getSave(){
       // isTermination = false;
        SQLite.select()
                .from(ComAll.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ComAll>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<ComAll> tResult) {
                        NLog.e(NLog.PLAYER, "SQLite  onListQueryResult 查询成功");
                        if(!CheckUtil.isEmpty(tResult)){
                            NLog.e(NLog.PLAYER, "SQLite  onListQueryResult size"+tResult.size());

                                comAllList.clear();
                                comAllList.addAll(tResult);

                                playHistory();//主动从数据库获取数据 进行播放

                        }

                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                        NLog.e(NLog.PLAYER, "SQLite  error"+error.getMessage());
                    }
                })
                .execute();

    }

    private Runnable saveData = new Runnable() {
        @Override
        public void run() {

            Delete.table(ComAll.class); //清空之前历史纪录

            for (ComAll comAll : comAllList){
                comAll.save();
            }

            NLog.e(NLog.PLAYER,"数据库保存完成");

        }
    };
//    private Runnable reqData = new Runnable() {
//        @Override
//        public void run() {
//            reqData(comAll.getId());//播放时检测到播放地址为空
//        }
//    };
    private Call call;
    private void reqData(String id){

        NLog.e(NLog.PLAYER, "通过接口  获取播放地址 id ：" + id);

        if(CheckUtil.isEmpty(id)){
            comAll = null;
            return;
        }
        if(null != call){
           // NLog.e(NLog.PLAYER,"path 通过接口获取  call 状态 : " + " isCanceled : " + call.isCanceled()+" isExecuted : " + call.isExecuted());

            call.cancel();
        }

        UseData useData = UseData.getUseData();

        String userName = useData.getUserName();
        String sessionKey = useData.getSessionkey();

        String url = BaseUrlONE+"app/audio.ashx?"+"id="+id+"&username="
                + userName+"&sessionkey="+sessionKey;

        NLog.e(NLog.PLAYER,"path 通过接口获取  url: " + url);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NLog.e(NLog.PLAYER,"path 通过接口获取：onFailure " +e.toString() + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful()){
                    ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                    String s = response.body().string();
                    NLog.e(NLog.PLAYER,"path 通过接口获取 json : " +s);

                    BaseDataResponse<ComAll> baseDataResponse = objectMapper.readValue(s, new TypeReference<BaseDataResponse<ComAll>>(){});

                    if(baseDataResponse.getCode() == 1){
                        ComAll comAll = baseDataResponse.getData();

                        if(!CheckUtil.isEmpty(comAll.getUrl())){
                            Message message = new Message();
                            message.what = REQDATA;
                            message.obj = baseDataResponse;
                            mainPlayerHandler.sendMessage(message);
                        }
                    }
                }else {

                }

            }
        });
    }

    //上传 播放次数
    private void setReqPta(){

                if(null == comAll)
                    return;

                String url = BaseUrlONE+"app/pta.ashx";


                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build();

                FormBody.Builder builder = new FormBody.Builder();
                builder.add("mid", comAll.getId());
                RequestBody formBody = builder.build();

                final Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)//默认就是GET请求，可以不写
                        .build();
                Call call = okHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        NLog.e(NLog.PLAYER, "上传播放次数 onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if(response.isSuccessful()){
                            String s= response.body().string();
                            NLog.e(NLog.PLAYER, "上传播放次数 onResponse: " + s);
                        }else {
                            NLog.e(NLog.PLAYER, "上传播放次数 失败了");
                        }

                    }
                });

    }

    // MusicPlayerHandler 消息传递 子线程

    public void setUpdataNUll(BaseDataResponse<ComAll> baseDataResponse){
        comAll = baseDataResponse.getData();

        comAllList.set(playNumber, comAll);

        pushAction(UPDATA_ID,comAll.getId());
        setReqPta();//更换地址时

      //  mPlayerHandler.post(this.saveData);
        playQQQ(comAll.getUrl());//获取地址后直接播放
    }

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {

            if(multiPlayer == null)
                return;

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(true){
                        boolean is = isPlaying();
                        NLog.e(NLog.PLAYER,"播放器正在播放isPlaying " + is);
                        if (is) {
                            pause();//多媒体限制
                        }
                    }

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if(true){
                        boolean is = isPlaying();
                        NLog.e(NLog.PLAYER,"播放器正在播放isPlaying " + is);
                        if (is) {
                            multiPlayer.setVolume(0.1f);
                        }
                    }

                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    multiPlayer.setVolume(1.0f);
                    start();//多媒体限制过后自动播放
                    break;
                default:
            }
        }
    };

    //接受广播 锁屏。。。  之类
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {

           // L.E("广播  onreceive");

            handleCommandIntent(intent);//广播

        }
    };

}
